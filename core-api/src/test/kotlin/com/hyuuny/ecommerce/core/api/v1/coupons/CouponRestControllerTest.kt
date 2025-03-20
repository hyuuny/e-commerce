package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.error.ErrorCode
import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.coupons.*
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class CouponRestControllerTest(
    @LocalServerPort val port: Int,
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        deleteAllUser()
        couponRepository.deleteAll()
        userCouponRepository.deleteAll()
    }

    @Test
    fun `최대 발급수가 100개인 쿠폰을 101명의 사용자가 동시에 요청하면 1명은 발급받을 수 없다`() {
        val now = LocalDate.now()
        val wonCoupon = WonCouponEntity(
            couponType = CouponType.ALL_DISCOUNT,
            code = "WON_COUPON",
            name = "5천원 할인 쿠폰",
            expiredDate = now.plusDays(7),
            fromDate = now.plusDays(1),
            toDate = now.plusDays(8),
            minimumOrderPrice = MinimumOrderPrice.ZERO,
            maximumDiscountPrice = MaximumDiscountPrice(0),
            firstComeFirstServed = true,
            maxIssuanceCount = 100,
            currentIssuedCount = 0,
            discountPrice = DiscountPrice(5000)
        )
        couponRepository.save(wonCoupon)

        val executor: ExecutorService = Executors.newFixedThreadPool(101)
        val tasks: MutableList<Callable<Boolean>> = mutableListOf()

        for (i in 1..101) {
            val request = IssueUserCouponReqeustDto(
                userId = i.toLong(),
                couponId = wonCoupon.id,
                discountType = DiscountType.WON,
            )

            tasks.add(Callable {
                val response = Given {
                    contentType(ContentType.JSON)
                    header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
                    body(request)
                    log().all()
                } When {
                    post("/api/v1/coupons")
                } Then {
                    log().all()
                }

                if (i <= 100) {
                    response.statusCode(HttpStatus.SC_OK)
                    response.body("result", equalTo(ResultType.SUCCESS.name))
                    response.body("data.userId", equalTo(request.userId.toInt()))
                    response.body("data.couponId", equalTo(request.couponId.toInt()))
                    response.body("data.publishedDate", equalTo(LocalDate.now()))
                    response.body("data.expiredDate", equalTo(wonCoupon.expiredDate))
                    response.body("data.used", equalTo(false))
                    response.body("data.usedDateTime", equalTo(null))
                    return@Callable true
                } else {
                    response.statusCode(HttpStatus.SC_BAD_REQUEST)
                    response.body("error.code", equalTo(ErrorCode.E400.name))
                    response.body("error.message", equalTo(ErrorType.OVER_COUPON_MAXISSUANCE_COUNT.message))
                    response.body("error.data", equalTo(null))
                    return@Callable false
                }
            })
        }
        val futures: List<Future<Boolean>> = executor.invokeAll(tasks)

        val results = futures.map { it.get() }
        val successCount = results.count { it }
        val failureCount = results.count { !it }
        val wonCouponEntity = couponRepository.findByIdOrNull(wonCoupon.id)!!

        assertThat(wonCouponEntity.currentIssuedCount).isEqualTo(100)
        assertThat(successCount).isEqualTo(100)
        assertThat(failureCount).isEqualTo(1)

        executor.shutdown()
    }

    @Test
    fun `존재하지 않는 쿠폰은 사용자에게 발행할 수 없다`() {
        val invalidId = 99999L
        val request = IssueUserCouponReqeustDto(
            userId = 1L,
            couponId = invalidId,
            discountType = DiscountType.WON
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            body(request)
            log().all()
        } When {
            post("/api/v1/coupons")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.COUPON_NOT_FOUND.message))
            body("error.data", equalTo("쿠폰을 찾을 수 없습니다. id: $invalidId"))
            log().all()
        }
    }
}