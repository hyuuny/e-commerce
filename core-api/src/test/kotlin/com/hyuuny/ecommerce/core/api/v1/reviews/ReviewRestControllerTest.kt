package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductRepository
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.ON_SALE
import com.hyuuny.ecommerce.storage.db.core.catalog.products.StockQuantity
import com.hyuuny.ecommerce.storage.db.core.orders.DiscountPrice
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemRepository
import com.hyuuny.ecommerce.storage.db.core.orders.Price
import com.hyuuny.ecommerce.storage.db.core.orders.TotalPrice
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoRepository
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewRepository
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
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
import com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice as ProductsDiscountPrice
import com.hyuuny.ecommerce.storage.db.core.catalog.products.Price as ProductsPrice

class ReviewRestControllerTest(
    @LocalServerPort val port: Int,
    private val service: ReviewService,
    private val repository: ReviewRepository,
    private val reviewPhotoRepository: ReviewPhotoRepository,
    private val orderITemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        reviewPhotoRepository.deleteAll()
        repository.deleteAll()
        orderITemRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun `사용자는 주문 상품에 후기를 작성할 수 있다`() {
        val productEntity = productRepository.save(
            ProductEntity(
                1,
                ON_SALE,
                "product-1",
                "thumbnail.png",
                ProductsPrice(20000),
                ProductsDiscountPrice(1000),
                StockQuantity(100)
            )
        )
        val orderItemEntity = orderITemRepository.save(
            OrderItemEntity(
                orderId = 1,
                productId = productEntity.id,
                productName = "[2024 어워즈/마스크팩 1등] 토리든 다이브인 저분자 히알루론산 마스크 8매 어워즈 한정기획 (+셀메이징 2매)",
                quantity = 1,
                price = Price(10000),
                discountPrice = DiscountPrice(2000),
                totalPrice = TotalPrice(8000)
            )
        )
        val request = WriteReviewRequestDto(
            userId = 1,
            orderItemId = orderItemEntity.id,
            productId = orderItemEntity.productId,
            content = "1. 마스크 밀착력은 평타는 하며 에센스 양도 적당해서 목부분까지 마르고도 남을양입니다.\n" +
                    "\n" +
                    "2. 평상시에 1일 1팩보다는 피부가 예민하거나 건조하실때 사용을 하시면 큰 도움이 됩니다. 각질 일어났던 부분이 진정이 됩니다.\n" +
                    "\n" +
                    "3. 자극없는 순한제품이어서 피부타입없이 누구나 사용가능합니다.\n" +
                    "\n" +
                    "결론은 피부가 건조하실때 사용을 하시면 됩니다. 피부타입에 상관없이 사용가능합니다.",
            score = 5,
            photos = listOf(
                WriteReviewPhotoRequestDto(photoUrl = "http://ecommerce.hyuuny.com/photo-1.jpg"),
                WriteReviewPhotoRequestDto(photoUrl = "http://ecommerce.hyuuny.com/photo-2.jpg"),
                WriteReviewPhotoRequestDto(photoUrl = "http://ecommerce.hyuuny.com/photo-3.jpg"),
            )
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            body(request)
            log().all()
        } When {
            post("/api/v1/reviews")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.type", equalTo(ReviewType.PHOTO.name))
            body("data.userId", equalTo(request.userId.toInt()))
            body("data.orderItemId", equalTo(request.orderItemId.toInt()))
            body("data.productId", equalTo(request.productId.toInt()))
            body("data.content", equalTo(request.content))
            body("data.score", equalTo(request.score))
            body("data.photos.size()", equalTo(request.photos.size))
            body("data.photos[0].photoUrl", equalTo(request.photos[0].photoUrl))
            body("data.photos[1].photoUrl", equalTo(request.photos[1].photoUrl))
            body("data.photos[2].photoUrl", equalTo(request.photos[2].photoUrl))
            log().all()
        }
    }
}
