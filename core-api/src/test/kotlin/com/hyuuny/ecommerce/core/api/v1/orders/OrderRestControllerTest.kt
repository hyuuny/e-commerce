package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.error.ErrorCode
import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice
import com.hyuuny.ecommerce.storage.db.core.catalog.products.Price
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.ON_SALE
import com.hyuuny.ecommerce.storage.db.core.orders.*
import com.hyuuny.ecommerce.storage.db.core.utils.CodeGenerator
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
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import java.time.LocalDateTime

class OrderRestControllerTest(
    @LocalServerPort val port: Int,
    private val repository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val service: OrderService,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
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
            body("data.totalProductAmount", equalTo(request.totalProductAmount.toInt()))
            body("data.totalDiscountAmount", equalTo(request.totalDiscountAmount.toInt()))
            body("data.shippingFee", equalTo(request.shippingFee.toInt()))
            body("data.totalAmount", equalTo(request.totalAmount.toInt()))
            body("data.items[0].status", equalTo(OrderItemStatus.CREATED.name))
            body("data.items[0].productId", equalTo(request.items[0].productId.toInt()))
            body("data.items[0].productName", equalTo(productEntities[0].name))
            body("data.items[0].quantity", equalTo(request.items[0].quantity.toInt()))
            body("data.items[0].discountAmount", equalTo(request.items[0].discountAmount.toInt()))
            body("data.items[0].amount", equalTo(request.items[0].amount.toInt()))
            body("data.items[0].totalAmount", equalTo(productEntities[0].calculateTotalPrice().times(1).toInt()))
            body("data.items[1].status", equalTo(OrderItemStatus.CREATED.name))
            body("data.items[1].productId", equalTo(request.items[1].productId.toInt()))
            body("data.items[1].productName", equalTo(productEntities[1].name))
            body("data.items[1].quantity", equalTo(request.items[1].quantity.toInt()))
            body("data.items[1].discountAmount", equalTo(request.items[1].discountAmount.toInt()))
            body("data.items[1].amount", equalTo(request.items[1].amount.toInt()))
            body("data.items[1].totalAmount", equalTo(productEntities[1].calculateTotalPrice().times(2).toInt()))
            log().all()
        }
    }

    @Test
    fun `주문시 제품의 재고가 부족하면 주문에 실패한다`() {
        val productEntity = productRepository.save(
            ProductEntity(
                1, ON_SALE, "product-1", "thumbnail-1.png", Price(20000),
                DiscountPrice(1000), StockQuantity(100)
            )
        )
        val checkoutItem = listOf(
            CheckoutItemRequestDto(
                productId = productEntity.id,
                quantity = 150,
                discountAmount = productEntity.discountPrice.discountAmount * 150,
                amount = productEntity.price.amount * 150,
                totalAmount = productEntity.calculateTotalPrice(),
            )
        )
        val request = CheckoutRequestDto(
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
            totalProductAmount = checkoutItem[0].amount,
            totalDiscountAmount = checkoutItem[0].discountAmount,
            shippingFee = 3000,
            totalAmount = checkoutItem[0].totalAmount,
            items = checkoutItem
        )

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            body(request)
            log().all()
        } When {
            post("/api/v1/orders")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("result", equalTo("ERROR"))
            body("data", equalTo(null))
            body("error.code", equalTo(ErrorCode.E400.name))
            body("error.message", equalTo(ErrorType.INSUFFICIENT_STOCK_EXCEPTION.message))
            body("error.data", equalTo("상품 재고가 부족합니다. id: ${productEntity.id}"))
            log().all()
        }
    }

    @Test
    fun `주문 내역을 상세조회 할 수 있다`() {
        val productEntities = generateProductEntities()
        val checkoutItems = listOf(
            CheckoutItem(
                productId = productEntities[0].id,
                quantity = 1,
                discountAmount = productEntities[0].discountPrice.discountAmount * 1,
                amount = productEntities[0].price.amount * 1,
                totalAmount = productEntities[0].calculateTotalPrice() * 1
            ),
            CheckoutItem(
                productId = productEntities[1].id,
                quantity = 2,
                discountAmount = productEntities[1].discountPrice.discountAmount * 2,
                amount = productEntities[1].price.amount * 2,
                totalAmount = productEntities[1].calculateTotalPrice() * 2
            ),
        )
        val totalProductPrice = checkoutItems[0].amount + checkoutItems[1].amount
        val totalDiscountAmount = checkoutItems[0].discountAmount + checkoutItems[1].discountAmount
        val shippingFee = 3000L
        val totalPrice = totalProductPrice - totalDiscountAmount + shippingFee
        val command = Checkout(
            userId = 1,
            orderer = OrdererCommand(
                name = "김성현",
                phoneNumber = "01012341234"
            ),
            deliveryDetail = DeliveryDetailCommand(
                address = "서울시 구로구 123",
                addressDetail = "3동 503호",
                recipientName = "김성현",
                message = "문앞 보관해주세요!"
            ),
            totalProductAmount = totalProductPrice,
            totalDiscountAmount = totalDiscountAmount,
            shippingFee = shippingFee,
            totalAmount = totalPrice,
            items = checkoutItems
        )
        val newOrder = service.checkout(command)

        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/orders/${newOrder.id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", equalTo(newOrder.id.toInt()))
            body("data.orderCode", equalTo(newOrder.orderCode))
            body("data.status", equalTo(newOrder.status.name))
            body("data.userId", equalTo(newOrder.userId.toInt()))
            body("data.orderer.name", equalTo(newOrder.orderer.name))
            body("data.orderer.phoneNumber", equalTo(newOrder.orderer.phoneNumber))
            body("data.deliveryDetail.address", equalTo(newOrder.deliveryDetailData.address))
            body("data.deliveryDetail.addressDetail", equalTo(newOrder.deliveryDetailData.addressDetail))
            body("data.deliveryDetail.recipientName", equalTo(newOrder.deliveryDetailData.recipientName))
            body("data.deliveryDetail.message", equalTo(newOrder.deliveryDetailData.message))
            body("data.totalProductAmount", equalTo(newOrder.totalProductPrice.totalProductAmount.toInt()))
            body("data.totalDiscountAmount", equalTo(newOrder.totalDiscountPrice.totalDiscountAmount.toInt()))
            body("data.shippingFee", equalTo(newOrder.shippingFee.toInt()))
            body("data.totalAmount", equalTo(newOrder.totalPrice.totalAmount.toInt()))
            body("data.items[0].orderId", equalTo(newOrder.id.toInt()))
            body("data.items[0].status", equalTo(OrderItemStatus.CREATED.name))
            body("data.items[0].productId", equalTo(newOrder.items[0].productId.toInt()))
            body("data.items[0].productName", equalTo(productEntities[0].name))
            body("data.items[0].quantity", equalTo(newOrder.items[0].quantity.toInt()))
            body("data.items[0].discountAmount", equalTo(newOrder.items[0].discountPrice.discountAmount.toInt()))
            body("data.items[0].amount", equalTo(newOrder.items[0].price.amount.toInt()))
            body("data.items[0].totalAmount", equalTo(productEntities[0].calculateTotalPrice().times(1).toInt()))
            body("data.items[1].orderId", equalTo(newOrder.id.toInt()))
            body("data.items[1].status", equalTo(OrderItemStatus.CREATED.name))
            body("data.items[1].productId", equalTo(newOrder.items[1].productId.toInt()))
            body("data.items[1].productName", equalTo(productEntities[1].name))
            body("data.items[1].quantity", equalTo(newOrder.items[1].quantity.toInt()))
            body("data.items[1].discountAmount", equalTo(newOrder.items[1].discountPrice.discountAmount.toInt()))
            body("data.items[1].amount", equalTo(newOrder.items[1].price.amount.toInt()))
            body("data.items[1].totalAmount", equalTo(productEntities[1].calculateTotalPrice().times(2).toInt()))
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 주문을 상세조회 할 수 없다`() {
        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            log().all()
        } When {
            get("/api/v1/orders/$INVALID_ID")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo(ResultType.ERROR.name))
            body("data", equalTo(null))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.ORDER_NOT_FOUND_EXCEPTION.message))
            body("error.data", equalTo("주문을 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }

    @Test
    fun `주문 내역 목록을 조회할 수 있다`() {
        val orderEntities = repository.saveAll(
            listOf(
                generateOrderEntity(100000, 1000, 99000),
                generateOrderEntity(100000, 2000, 98000),
                generateOrderEntity(100000, 3000, 97000),
                generateOrderEntity(100000, 4000, 96000),
                generateOrderEntity(100000, 5000, 95000),
                generateOrderEntity(100000, 6000, 94000),
                generateOrderEntity(100000, 7000, 93000),
                generateOrderEntity(100000, 8000, 92000),
                generateOrderEntity(100000, 9000, 91000),
                generateOrderEntity(100000, 10000, 90000),
                generateOrderEntity(100000, 11000, 89000),
                generateOrderEntity(100000, 12000, 88000),
                generateOrderEntity(100000, 13000, 87000),
            )
        )
        orderItemRepository.saveAll(orderEntities.map { order ->
            OrderItemEntity(
                orderId = order.id,
                productId = 1,
                productName = "product-1",
                quantity = 1,
                price = com.hyuuny.ecommerce.storage.db.core.orders.Price(order.totalProductPrice.totalProductAmount),
                discountPrice = com.hyuuny.ecommerce.storage.db.core.orders.DiscountPrice(order.totalDiscountPrice.totalDiscountAmount),
                totalPrice = TotalPrice(order.totalPrice.totalAmount)
            )
        })

        orderEntities.sortByDescending { it.id }
        Given {
            contentType(ContentType.JSON)
            header(HttpHeaders.AUTHORIZATION, generateJwtToken(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD))
            param("page", 0)
            param("size", 10)
            param("sort", "id,desc")
            param("userId", "1")
        } When {
            get("/api/v1/orders")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content.size()", equalTo(10))
            orderEntities.take(10).forEachIndexed { index, order ->
                body("data.content[$index].id", equalTo(order.id.toInt()))
                body("data.content[$index].orderCode", equalTo(order.orderCode))
                body("data.content[$index].status", equalTo(order.status.name))
                body("data.content[$index].userId", equalTo(order.userId.toInt()))
                body(
                    "data.content[$index].totalProductAmount",
                    equalTo(order.totalProductPrice.totalProductAmount.toInt())
                )
                body(
                    "data.content[$index] .totalDiscountAmount",
                    equalTo(order.totalDiscountPrice.totalDiscountAmount.toInt())
                )
                body("data.content[$index].shippingFee", equalTo(order.shippingFee.toInt()))
                body("data.content[$index].totalAmount", equalTo(order.totalPrice.totalAmount.toInt()))
                body("data.content[$index].items.size()", equalTo(1))
            }
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(false))
            log().all()
        }
    }

    private fun generateOrderEntity(totalProductPrice: Long, totalDiscountPrice: Long, totalPrice: Long) = OrderEntity(
        orderCode = CodeGenerator.generateOrderCode(LocalDateTime.now()),
        userId = 1,
        orderer = Orderer(
            name = "김성현",
            phoneNumber = "01012341234"
        ),
        deliveryDetail = DeliveryDetail(
            address = "서울시 구로구 123",
            addressDetail = "3동 503호",
            recipientName = "김성현",
            message = "문앞 보관해주세요!"
        ),
        totalProductPrice = TotalProductPrice(totalProductPrice),
        totalDiscountPrice = TotalDiscountPrice(totalDiscountPrice),
        shippingFee = 3000L,
        totalPrice = TotalPrice(totalPrice),
    )

    private fun generateCheckoutRequest(checkoutItems: List<CheckoutItemRequestDto>): CheckoutRequestDto {
        val totalProductPrice = checkoutItems[0].amount + checkoutItems[1].amount
        val totalDiscountAmount = checkoutItems[0].discountAmount + checkoutItems[1].discountAmount
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
            totalProductAmount = totalProductPrice,
            totalDiscountAmount = totalDiscountAmount,
            shippingFee = shippingFee,
            totalAmount = totalPrice,
            items = checkoutItems
        )
    }

    private fun generateCheckoutItemRequest(productEntities: List<ProductEntity>) =
        listOf(
            CheckoutItemRequestDto(
                productId = productEntities[0].id,
                quantity = 1,
                discountAmount = productEntities[0].discountPrice.discountAmount * 1,
                amount = productEntities[0].price.amount * 1,
                totalAmount = productEntities[0].calculateTotalPrice() * 1
            ),
            CheckoutItemRequestDto(
                productId = productEntities[1].id,
                quantity = 2,
                discountAmount = productEntities[1].discountPrice.discountAmount * 2,
                amount = productEntities[1].price.amount * 2,
                totalAmount = productEntities[1].calculateTotalPrice() * 2
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
