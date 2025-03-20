package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.*
import com.hyuuny.ecommerce.storage.db.core.coupons.*
import com.hyuuny.ecommerce.storage.db.core.likes.LikeEntity
import com.hyuuny.ecommerce.storage.db.core.likes.LikeRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import java.time.LocalDate
import java.time.LocalDateTime
import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountPrice as WonCouponDiscountPrice

class MyRestControllerTest(
    @LocalServerPort val port: Int,
    private val likeRepository: LikeRepository,
    private val productRepository: ProductRepository,
    private val productBadgeRepository: ProductBadgeRepository,
    private var userCouponRepository: UserCouponRepository,
    private var couponRepository: CouponRepository,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        likeRepository.deleteAll()
        productBadgeRepository.deleteAll()
        productRepository.deleteAll()
        userCouponRepository.deleteAll()
        couponRepository.deleteAll()
    }

    @Test
    fun `나의 좋아요 목록을 조회할 수 있다`() {
        val userId = 1L
        val productEntities = productRepository.saveAll(
            listOf(
                ProductEntity(
                    1, ON_SALE, "product-1", "thumbnail-1.png", Price(20000),
                    DiscountPrice(1000), StockQuantity(100)
                ),
                ProductEntity(
                    2, ON_SALE, "product-2", "thumbnail-2.png", Price(20000),
                    DiscountPrice(2000), StockQuantity(200)
                ),
                ProductEntity(
                    3, ON_SALE, "product-3", "thumbnail-3.png", Price(20000),
                    DiscountPrice(3000), StockQuantity(300)
                ),
                ProductEntity(
                    4, SOLD_OUT, "product-4", "thumbnail-4.png", Price(20000),
                    DiscountPrice(4000), StockQuantity(300)
                ),
                ProductEntity(
                    5, ON_SALE, "product-5", "thumbnail-5.png", Price(20000),
                    DiscountPrice(5000), StockQuantity(300)
                ),
                ProductEntity(
                    5, STOPPED_SELLING, "product-6", "thumbnail-6.png", Price(20000),
                    DiscountPrice(6000), StockQuantity(300)
                ),
                ProductEntity(
                    6, ON_SALE, "product-7", "thumbnail-7.png", Price(20000),
                    DiscountPrice(7000), StockQuantity(300)
                ),
                ProductEntity(
                    3, ON_SALE, "product-8", "thumbnail-3.png", Price(20000),
                    DiscountPrice(8000), StockQuantity(300)
                ),
                ProductEntity(
                    1, STOPPED_SELLING, "product-9", "thumbnail-9.png", Price(20000),
                    DiscountPrice(9000), StockQuantity(300)
                ),
                ProductEntity(
                    7, ON_SALE, "product-10", "thumbnail-10.png", Price(20000),
                    DiscountPrice(10000), StockQuantity(300)
                ),
                ProductEntity(
                    8, ON_SALE, "product-11", "thumbnail-11.png", Price(20000),
                    DiscountPrice(11000), StockQuantity(300)
                ),
                ProductEntity(
                    2, ON_SALE, "product-12", "thumbnail-12.png", Price(20000),
                    DiscountPrice(12000), StockQuantity(300)
                ),
            )
        )
        productEntities.forEach {
            productBadgeRepository.saveAll(
                listOf(
                    ProductBadgeEntity(productId = it.id, title = "오늘드림", color = "#FFC0CB", bgColor = "#DCDCDC"),
                    ProductBadgeEntity(productId = it.id, title = "BEST", color = "#565656", bgColor = "#DCDCDC"),
                    ProductBadgeEntity(productId = it.id, title = "증정", color = "#565656", bgColor = "#DCDCDC"),
                )
            )
        }
        productEntities.forEach { likeRepository.save(LikeEntity(userId, it.id)) }

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            param("page", 0)
            param("size", 10)
            param("sort", "id,desc")
            log().all()
        } When {
            get("/api/v1/my/liked-products")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content.size()", equalTo(10))
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(false))
            log().all()
        }
    }

    @Test
    fun `사용자가 발급받은 쿠폰 중 아직 사용하지 않은 쿠폰 목록을 조회할 수 있다`() {
        val userId = 1L
        val couponEntities = couponRepository.saveAll(
            listOf(
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "첫번째 쿠폰",
                    name = "첫번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(7),
                    fromDate = LocalDate.now().minusDays(10),
                    toDate = LocalDate.now().minusDays(3),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "두번째 쿠폰",
                    name = "두번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(7),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(8),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = true,
                    maxIssuanceCount = 1000,
                    currentIssuedCount = 54,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "세번째 쿠폰",
                    name = "세번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(10),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(11),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "네번째 쿠폰",
                    name = "네번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(19),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(20),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = true,
                    maxIssuanceCount = 2000,
                    currentIssuedCount = 1300,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "다섯번째 쿠폰",
                    name = "다섯째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(8),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(9),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
            )
        )
        userCouponRepository.saveAll(
            listOf(
                UserCouponEntity(
                    userId,
                    couponEntities[0].id,
                    LocalDate.now().minusDays(5),
                    couponEntities[0].expiredDate,
                    true,
                    LocalDateTime.now().minusDays(3),
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[1].id,
                    LocalDate.now(),
                    couponEntities[1].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[2].id,
                    LocalDate.now(),
                    couponEntities[2].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[3].id,
                    LocalDate.now(),
                    couponEntities[3].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[4].id,
                    LocalDate.now(),
                    couponEntities[4].expiredDate,
                    false,
                    null
                ),
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            param("userId", userId)
            param("used", false)
            param("page", 0)
            param("size", 10)
            param("sort", "expiredDate,desc")
            log().all()
        } When {
            get("/api/v1/my/coupons")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content.size()", equalTo(4))
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(true))
            log().all()
        }
    }

    @Test
    fun `사용자가 발급받은 쿠폰 중 사용한 쿠폰 목록을 조회할 수 있다`() {
        val userId = 1L
        val couponEntities = couponRepository.saveAll(
            listOf(
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "첫번째 쿠폰",
                    name = "첫번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(7),
                    fromDate = LocalDate.now().minusDays(10),
                    toDate = LocalDate.now().minusDays(3),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "두번째 쿠폰",
                    name = "두번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(7),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(8),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = true,
                    maxIssuanceCount = 1000,
                    currentIssuedCount = 54,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "세번째 쿠폰",
                    name = "세번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(10),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(11),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "네번째 쿠폰",
                    name = "네번째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(19),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(20),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = true,
                    maxIssuanceCount = 2000,
                    currentIssuedCount = 1300,
                ),
                WonCouponEntity(
                    couponType = CouponType.ALL_DISCOUNT,
                    code = "다섯번째 쿠폰",
                    name = "다섯째 쿠폰입니다",
                    expiredDate = LocalDate.now().plusDays(8),
                    fromDate = LocalDate.now().plusDays(1),
                    toDate = LocalDate.now().plusDays(9),
                    minimumOrderPrice = MinimumOrderPrice.ZERO,
                    maximumDiscountPrice = null,
                    discountPrice = WonCouponDiscountPrice(5000),
                    firstComeFirstServed = false,
                    maxIssuanceCount = null,
                    currentIssuedCount = null,
                ),
            )
        )
        userCouponRepository.saveAll(
            listOf(
                UserCouponEntity(
                    userId,
                    couponEntities[0].id,
                    LocalDate.now().minusDays(5),
                    couponEntities[0].expiredDate,
                    true,
                    LocalDateTime.now().minusDays(3),
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[1].id,
                    LocalDate.now(),
                    couponEntities[1].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[2].id,
                    LocalDate.now(),
                    couponEntities[2].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[3].id,
                    LocalDate.now(),
                    couponEntities[3].expiredDate,
                    false,
                    null
                ),
                UserCouponEntity(
                    userId,
                    couponEntities[4].id,
                    LocalDate.now(),
                    couponEntities[4].expiredDate,
                    false,
                    null
                ),
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            param("userId", userId)
            param("used", true)
            param("page", 0)
            param("size", 10)
            param("sort", "expiredDate,desc")
            log().all()
        } When {
            get("/api/v1/my/coupons")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content.size()", equalTo(1))
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(true))
            log().all()
        }
    }
}