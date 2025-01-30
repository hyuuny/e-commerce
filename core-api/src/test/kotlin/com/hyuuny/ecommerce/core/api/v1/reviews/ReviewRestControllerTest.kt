package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.TestContainer
import com.hyuuny.ecommerce.core.support.error.ErrorCode
import com.hyuuny.ecommerce.core.support.error.ErrorType
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
import com.hyuuny.ecommerce.storage.db.core.reviews.*
import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
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
import kotlin.random.Random
import com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice as ProductsDiscountPrice
import com.hyuuny.ecommerce.storage.db.core.catalog.products.Price as ProductsPrice

@TestContainer
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
        deleteAllUser()
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

    @Test
    fun `주문 상품이 존재하지 않으면 후기를 작성할 수 없다`() {
        val request = WriteReviewRequestDto(
            userId = 1,
            orderItemId = INVALID_ID,
            productId = 1,
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
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.ORDER_ITEM_NOT_FOUND.message))
            body("error.data", equalTo("주문 상품을 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }

    @Test
    fun `주문 상품의 상품이 존재하지 않으면 후기를 작성할 수 없다`() {
        val orderItemEntity = orderITemRepository.save(
            OrderItemEntity(
                orderId = 1,
                productId = INVALID_ID,
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
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.PRODUCT_NOT_FOUND.message))
            body("error.data", equalTo("상품을 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }

    @Test
    fun `후기를 상세 조회 할 수 있다`() {
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
                WriteReviewPhotoRequestDto("http://ecommerce.hyuuny.com/photo-1.jpg"),
                WriteReviewPhotoRequestDto("http://ecommerce.hyuuny.com/photo-2.jpg"),
                WriteReviewPhotoRequestDto("http://ecommerce.hyuuny.com/photo-3.jpg"),
            )
        )
        val newReview = service.write(request.toWriteReview())

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/reviews/${newReview.id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.type", equalTo(ReviewType.PHOTO.name))
            body("data.userId", equalTo(newReview.userId.toInt()))
            body("data.orderItemId", equalTo(newReview.orderItemId.toInt()))
            body("data.productId", equalTo(newReview.productId.toInt()))
            body("data.content", equalTo(newReview.content))
            body("data.score", equalTo(newReview.score.score))
            body("data.photos.size()", equalTo(newReview.photos.size))
            body("data.photos[0].photoUrl", equalTo(newReview.photos[0].photoUrl))
            body("data.photos[1].photoUrl", equalTo(newReview.photos[1].photoUrl))
            body("data.photos[2].photoUrl", equalTo(newReview.photos[2].photoUrl))
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 후기를 상세 조회 할 수 없다`() {
        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/reviews/$INVALID_ID")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.REVIEW_NOT_FOUND.message))
            body("error.data", equalTo("후기를 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }

    @Test
    fun `후기 목록을 조회할 수 있다`() {
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
        val users = userRepository.saveAll(
            listOf(
                UserEntity("newuser1@naver.com", "pwd123!", "가가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser2@naver.com", "pwd123!", "나가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser3@naver.com", "pwd123!", "다가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser4@naver.com", "pwd123!", "라가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser5@naver.com", "pwd123!", "마가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser6@naver.com", "pwd123!", "바가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser7@naver.com", "pwd123!", "사가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser8@naver.com", "pwd123!", "아가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser9@naver.com", "pwd123!", "자가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser10@naver.com", "pwd123!", "차가입", "01012345678", setOf(Role.CUSTOMER)),
                UserEntity("newuser11@naver.com", "pwd123!", "카가입", "01012345678", setOf(Role.CUSTOMER)),
            )
        )
        val productId = productEntity.id
        val reviews = users.map {
            repository.save(
                ReviewEntity(ReviewType.PHOTO, it.id, 1, productId, "content-${it.id}", Score(Random.nextInt(5) + 1)),
            )
        }
        reviews.forEach {
            reviewPhotoRepository.saveAll(
                listOf(
                    ReviewPhotoEntity(reviewId = it.id, photoUrl = "http://ecommerce.hyuuny.com/photo-${it.id}-1.jpg"),
                    ReviewPhotoEntity(reviewId = it.id, photoUrl = "http://ecommerce.hyuuny.com/photo-${it.id}-2.jpg"),
                    ReviewPhotoEntity(reviewId = it.id, photoUrl = "http://ecommerce.hyuuny.com/photo-${it.id}-3.jpg"),
                )
            )
        }

        Given {
            contentType(ContentType.JSON)
            params("page", 0)
            params("size", 10)
            params("sort", "id,DESC")
        } When {
            get("/api/v1/reviews")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content[0].userId", equalTo(users[10].id.toInt()))
            body("data.content[0].content", equalTo(reviews[10].content))
            body("data.content[0].score", equalTo(reviews[10].score.score))
            body("data.content[9].userId", equalTo(users[1].id.toInt()))
            body("data.content[9].content", equalTo(reviews[1].content))
            body("data.content[9].score", equalTo(reviews[1].score.score))
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(false))
            log().all()
        }
    }
}
