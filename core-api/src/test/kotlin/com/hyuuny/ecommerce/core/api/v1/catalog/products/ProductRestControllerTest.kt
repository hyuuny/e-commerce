package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.TestContainer
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.brands.BrandRepository
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.*
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

@TestContainer
class ProductRestControllerTest(
    @LocalServerPort val port: Int,
    private val repository: ProductRepository,
    private val bannerRepository: ProductBannerRepository,
    private val contentRepository: ProductContentRepository,
    private val badgeRepository: ProductBadgeRepository,
    private val brandRepository: BrandRepository,
    private val service: ProductService,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
        bannerRepository.deleteAll()
        contentRepository.deleteAll()
        badgeRepository.deleteAll()
        brandRepository.deleteAll()
        service.cacheEvict()
    }

    @Test
    fun `상품 목록을 조회할 수 있다`() {
        val products = repository.saveAll(
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
        badgeRepository.saveAll(
            listOf(
                ProductBadgeEntity(1, products[11].id, "오늘드림1", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(2, products[11].id, "오늘드림2", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(3, products[11].id, "오늘드림3", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(4, products[10].id, "오늘드림4", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(5, products[10].id, "오늘드림5", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(6, products[9].id, "오늘드림6", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(7, products[9].id, "오늘드림7", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(8, products[8].id, "오늘드림8", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(9, products[7].id, "오늘드림9", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(10, products[6].id, "오늘드림10", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(11, products[5].id, "오늘드림11", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(12, products[4].id, "오늘드림12", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(13, products[3].id, "오늘드림13", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(14, products[2].id, "오늘드림14", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(15, products[1].id, "오늘드림15", "#FFC0CB", "#DCDCDC"),
                ProductBadgeEntity(16, products[0].id, "오늘드림16", "#FFC0CB", "#DCDCDC"),
            )
        )

        Given {
            contentType(ContentType.JSON)
            params("page", 0)
            params("size", 10)
            params("sort", "id,DESC")
            log().all()
        } When {
            get("/api/v1/products")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content[0].name", equalTo(products[11].name))
            body("data.content[0].badges.size()", equalTo(3))
            body("data.content[1].name", equalTo(products[10].name))
            body("data.content[1].badges.size()", equalTo(2))
            body("data.content[2].name", equalTo(products[9].name))
            body("data.content[2].badges.size()", equalTo(2))
            body("data.content[3].name", equalTo(products[8].name))
            body("data.content[3].badges.size()", equalTo(1))
            body("data.content[4].name", equalTo(products[7].name))
            body("data.content[4].badges.size()", equalTo(1))
            body("data.content[5].name", equalTo(products[6].name))
            body("data.content[5].badges.size()", equalTo(1))
            body("data.content[6].name", equalTo(products[5].name))
            body("data.content[6].badges.size()", equalTo(1))
            body("data.content[7].name", equalTo(products[4].name))
            body("data.content[7].badges.size()", equalTo(1))
            body("data.content[8].name", equalTo(products[3].name))
            body("data.content[8].badges.size()", equalTo(1))
            body("data.content[9].name", equalTo(products[2].name))
            body("data.content[9].badges.size()", equalTo(1))
            log().all()
        }
    }
}