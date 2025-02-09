package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.*
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

class MyRestControllerTest(
    @LocalServerPort val port: Int,
    private val likeRepository: LikeRepository,
    private val productRepository: ProductRepository,
    private val productBadgeRepository: ProductBadgeRepository,
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
}