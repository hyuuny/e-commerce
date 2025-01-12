package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.ON_SALE
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemRepository
import com.hyuuny.ecommerce.storage.db.core.orders.OrderRepository
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders

class OrderRestControllerTest(
    @LocalServerPort val port: Int,
    private val repository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val service: OrderService,
) : BaseIntegrationTest() {
    private lateinit var redissonClient: RedissonClient

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        redissonClient = mockk()
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
        orderItemRepository.deleteAll()
        productRepository.deleteAll()
    }

    @Test
    fun `주문을 성공적으로 생성할 수 있다`() {
        val productEntities = generateProductEntities()
        val checkoutItems = generateCheckoutItemRequest(productEntities)
        val request = generateCheckoutRequest(checkoutItems)

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            body(request)
            log().all()
        } When {
            post("/api/v1/orders")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", notNullValue())
            body("data.orderCode", notNullValue())
            body("data.userId", equalTo(request.userId.toInt()))
            body("data.orderer.name", equalTo(request.orderer.name))
            body("data.orderer.phoneNumber", equalTo(request.orderer.phoneNumber))
            body("data.deliveryDetail.address", equalTo(request.deliveryDetail.address))
            body("data.deliveryDetail.addressDetail", equalTo(request.deliveryDetail.addressDetail))
            body("data.deliveryDetail.recipientName", equalTo(request.deliveryDetail.recipientName))
            body("data.deliveryDetail.message", equalTo(request.deliveryDetail.message))
            body("data.totalProductPrice", equalTo(request.totalProductPrice.toInt()))
            body("data.totalDiscountAmount", equalTo(request.totalDiscountAmount.toInt()))
            body("data.shippingFee", equalTo(request.shippingFee.toInt()))
            body("data.totalPrice", equalTo(request.totalPrice.toInt()))
            body("data.items[0].productId", equalTo(request.items[0].productId.toInt()))
            body("data.items[0].productName", equalTo(productEntities[0].name))
            body("data.items[0].quantity", equalTo(request.items[0].quantity.toInt()))
            body("data.items[0].discountPrice", equalTo(request.items[0].discountPrice.toInt()))
            body("data.items[0].price", equalTo(request.items[0].price.toInt()))
            body("data.items[0].totalPrice", equalTo(productEntities[0].calculateTotalPrice().times(1).toInt()))
            body("data.items[1].productId", equalTo(request.items[1].productId.toInt()))
            body("data.items[1].productName", equalTo(productEntities[1].name))
            body("data.items[1].quantity", equalTo(request.items[1].quantity.toInt()))
            body("data.items[1].discountPrice", equalTo(request.items[1].discountPrice.toInt()))
            body("data.items[1].price", equalTo(request.items[1].price.toInt()))
            body("data.items[1].totalPrice", equalTo(productEntities[1].calculateTotalPrice().times(2).toInt()))
            log().all()
        }
    }

    private fun generateCheckoutRequest(checkoutItems: List<CheckoutItemRequestDto>): CheckoutRequestDto {
        val totalProductPrice = checkoutItems[0].price + checkoutItems[1].price
        val totalDiscountAmount = checkoutItems[0].discountPrice + checkoutItems[1].discountPrice
        val shippingFee = 3000L
        val totalPrice = totalProductPrice - totalDiscountAmount + shippingFee

        return CheckoutRequestDto(
            userId = 1,
            orderer = OrdererRequestDto(
                name = "김성현",
                phoneNumber = "01012341234"
            ),
            deliveryDetail = DeliveryDetailRequestDto(
                address = "서울시 구로구 123",
                addressDetail = "3동 503호",
                recipientName = "김성현",
                message = "문앞 보관해주세요!"
            ),
            totalProductPrice = totalProductPrice,
            totalDiscountAmount = totalDiscountAmount,
            shippingFee = shippingFee,
            totalPrice = totalPrice,
            items = checkoutItems
        )
    }

    private fun generateCheckoutItemRequest(productEntities: List<ProductEntity>) =
        listOf(
            CheckoutItemRequestDto(
                productId = productEntities[0].id,
                quantity = 1,
                discountPrice = productEntities[0].discountPrice.discountAmount * 1,
                price = productEntities[0].price.amount * 1,
                totalPrice = productEntities[0].calculateTotalPrice() * 1
            ),
            CheckoutItemRequestDto(
                productId = productEntities[1].id,
                quantity = 2,
                discountPrice = productEntities[1].discountPrice.discountAmount * 2,
                price = productEntities[1].price.amount * 2,
                totalPrice = productEntities[1].calculateTotalPrice() * 2
            ),
        )

    private fun generateProductEntities() = productRepository.saveAll(
        listOf(
            ProductEntity(
                1, ON_SALE, "product-1", "thumbnail-1.png", Price(20000),
                DiscountPrice(1000), StockQuantity(100)
            ),
            ProductEntity(
                2, ON_SALE, "product-2", "thumbnail-2.png", Price(20000),
                DiscountPrice(2000), StockQuantity(200)
            ),
        )
    )
}
