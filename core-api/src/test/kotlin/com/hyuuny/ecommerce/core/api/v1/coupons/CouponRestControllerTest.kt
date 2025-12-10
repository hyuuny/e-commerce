package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.TestEnvironment
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
import org.hamcrest.CoreMatchers.notNullValue
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

@TestEnvironment
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

    @Test
    fun `회원은 자신의 쿠폰을 상세조회 할 수 있다`() {
        val userEntity = userRepository.findByEmail(DEFAULT_USER_EMAIL)!!
        val couponEntity = couponRepository.save(
            WonCouponEntity(
                couponType = CouponType.ALL_DISCOUNT,
                code = "5천원할인쿠폰",
                name = "5천원 할인 쿠폰입니다",
                expiredDate = LocalDate.now().plusDays(5),
                fromDate = LocalDate.now().minusDays(1),
                toDate = LocalDate.now().minusDays(6),
                minimumOrderPrice = MinimumOrderPrice(15000),
                maximumDiscountPrice = null,
                discountPrice = DiscountPrice(5000),
                firstComeFirstServed = false,
                maxIssuanceCount = null,
                currentIssuedCount = null,
            )
        )
        val userCouponEntity = userCouponRepository.save(
            UserCouponEntity(
                userId = userEntity.id,
                couponId = couponEntity.id,
                publishedDate = LocalDate.now(),
                expiredDate = couponEntity.expiredDate,
                used = false,
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/coupons/${couponEntity.id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", equalTo(userCouponEntity.id.toInt()))
            body("data.userId", equalTo(userCouponEntity.userId.toInt()))
            body("data.couponId", equalTo(userCouponEntity.couponId.toInt()))
            body("data.couponType", equalTo(couponEntity.couponType.name))
            body("data.couponName", equalTo(couponEntity.name))
            body("data.publishedDate", equalTo(userCouponEntity.publishedDate.toString()))
            body("data.fromDate", equalTo(couponEntity.fromDate.toString()))
            body("data.toDate", equalTo(couponEntity.toDate.toString()))
            body("data.minimumOrderPrice", equalTo(couponEntity.minimumOrderPrice.minimumOrderAmount.toInt()))
            body("data.maximumDiscountPrice", equalTo(couponEntity.maximumDiscountPrice?.maximumDiscountAmount?.toInt()))
            body("data.used", equalTo(userCouponEntity.used))
            body("data.usedDateTime", equalTo(userCouponEntity.usedDateTime))
            body("data.createdAt", notNullValue())
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 쿠폰으로 자신의 쿠폰을 상세조회 할 수 없다`() {
        val userEntity = userRepository.findByEmail(DEFAULT_USER_EMAIL)!!
        userCouponRepository.save(
            UserCouponEntity(
                userId = userEntity.id,
                couponId = INVALID_ID,
                publishedDate = LocalDate.now(),
                expiredDate = LocalDate.now().plusDays(7),
                used = false,
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/coupons/$INVALID_ID")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.COUPON_NOT_FOUND.message))
            body("error.data", equalTo("쿠폰을 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }

    @Test
    fun `사용자는 발급받지 않은 쿠폰을 상세조회 할 수 없다`() {
        val userEntity = userRepository.findByEmail(DEFAULT_USER_EMAIL)!!
        val couponEntity = couponRepository.save(
            WonCouponEntity(
                couponType = CouponType.ALL_DISCOUNT,
                code = "5천원할인쿠폰",
                name = "5천원 할인 쿠폰입니다",
                expiredDate = LocalDate.now().plusDays(5),
                fromDate = LocalDate.now().minusDays(1),
                toDate = LocalDate.now().minusDays(6),
                minimumOrderPrice = MinimumOrderPrice(15000),
                maximumDiscountPrice = null,
                discountPrice = DiscountPrice(5000),
                firstComeFirstServed = false,
                maxIssuanceCount = null,
                currentIssuedCount = null,
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/coupons/${couponEntity.id}")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.USER_COUPON_NOT_FOUND.message))
            body("error.data", equalTo("${userEntity.id} 번 회원의 ${couponEntity.id} 번 쿠폰을 찾을 수 없습니다."))
            log().all()
        }
    }
}