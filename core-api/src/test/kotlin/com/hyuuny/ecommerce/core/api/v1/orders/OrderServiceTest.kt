package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.core.support.error.InsufficientStockException
import com.hyuuny.ecommerce.core.support.error.OrderNotFoundException
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.ON_SALE
import com.hyuuny.ecommerce.storage.db.core.catalog.products.StockQuantity
import com.hyuuny.ecommerce.storage.db.core.orders.*
import com.hyuuny.ecommerce.storage.db.core.utils.CodeGenerator
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class OrderServiceTest {
    private lateinit var writer: OrderWriter
    private lateinit var orderItemWriter: OrderItemWriter
    private lateinit var reader: OrderReader
    private lateinit var orderItemReader: OrderItemReader
    private lateinit var productReader: ProductReader
    private lateinit var service: OrderService

    private val tryLockTime = 10L

    @BeforeEach
    fun setUp() {
        writer = mockk()
        orderItemWriter = mockk()
        reader = mockk()
        orderItemReader = mockk()
        productReader = mockk()
        service = OrderService(writer, orderItemWriter, reader, orderItemReader, productReader)
    }

    @Test
    fun `주문을 성공적으로 생성할 수 있다`() {
        val productEntities = generateProductEntities()
        val checkoutItems = generateCheckoutItems(productEntities)
        val command = generateCheckout(checkoutItems)
        val orderEntity = generateOrderEntity(command)
        val orderItemEntities = checkoutItems.mapIndexed { index, item ->
            OrderItemEntity(
                orderId = orderEntity.id,
                productId = item.productId,
                productName = productEntities[index].name,
                quantity = item.quantity,
                discountPrice = DiscountPrice(item.discountAmount),
                price = Price(item.amount),
                totalPrice = TotalPrice(item.totalAmount)
            )
        }
        every { productReader.readAllByIds(any()) } returns productEntities
        every { writer.checkout(any()) } returns orderEntity
        every { orderItemWriter.append(any(), any(), any()) } returns orderItemEntities

        val newOrder = service.checkout(command)

        assertThat(newOrder.orderCode).isEqualTo(orderEntity.orderCode)
        assertThat(newOrder.status).isEqualTo(orderEntity.status)
        assertThat(newOrder.userId).isEqualTo(orderEntity.userId)
        assertThat(newOrder.orderer.name).isEqualTo(orderEntity.orderer.name)
        assertThat(newOrder.orderer.phoneNumber).isEqualTo(orderEntity.orderer.phoneNumber)
        assertThat(newOrder.deliveryDetailData.address).isEqualTo(orderEntity.deliveryDetail.address)
        assertThat(newOrder.deliveryDetailData.addressDetail).isEqualTo(orderEntity.deliveryDetail.addressDetail)
        assertThat(newOrder.deliveryDetailData.recipientName).isEqualTo(orderEntity.deliveryDetail.recipientName)
        assertThat(newOrder.deliveryDetailData.message).isEqualTo(orderEntity.deliveryDetail.message)
        assertThat(newOrder.totalProductPrice).isEqualTo(orderEntity.totalProductPrice)
        assertThat(newOrder.totalDiscountPrice).isEqualTo(orderEntity.totalDiscountPrice)
        assertThat(newOrder.shippingFee).isEqualTo(orderEntity.shippingFee)
        assertThat(newOrder.totalPrice).isEqualTo(orderEntity.totalPrice)
        assertThat(newOrder.items).hasSize(2)
        newOrder.items.forEachIndexed { index, item ->
            assertThat(item.orderId).isEqualTo(orderItemEntities[index].orderId)
            assertThat(item.productName).isEqualTo(orderItemEntities[index].productName)
            assertThat(item.quantity).isEqualTo(orderItemEntities[index].quantity)
            assertThat(item.discountPrice).isEqualTo(orderItemEntities[index].discountPrice)
            assertThat(item.price).isEqualTo(orderItemEntities[index].price)
            assertThat(item.totalPrice).isEqualTo(orderItemEntities[index].totalPrice)
        }
    }

    @Test
    fun `주문시 제품의 재고가 부족하면 주문에 실패한다`() {
        val productEntity = ProductEntity(
            1,
            ON_SALE,
            "product-1",
            "thumbnail-1.png",
            com.hyuuny.ecommerce.storage.db.core.catalog.products.Price(20000),
            com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice(1000),
            StockQuantity(100)
        )
        val checkoutItem = listOf(
            CheckoutItem(
                productId = productEntity.id,
                quantity = 150,
                discountAmount = productEntity.discountPrice.discountAmount * 150,
                amount = productEntity.price.amount * 150,
                totalAmount = productEntity.calculateTotalPrice(),
            )
        )
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
            totalProductAmount = checkoutItem[0].amount,
            totalDiscountAmount = checkoutItem[0].discountAmount,
            shippingFee = 3000,
            totalAmount = checkoutItem[0].totalAmount,
            items = checkoutItem
        )
        val orderEntity = generateOrderEntity(command)

        every { productReader.readAllByIds(any()) } returns listOf(productEntity)
        every { writer.checkout(any()) } returns orderEntity
        every { orderItemWriter.append(any(), any(), any()) } throws
                InsufficientStockException("상품 재고가 부족합니다. id: ${productEntity.id}")

        val exception = assertThrows<InsufficientStockException> {
            service.checkout(command)
        }

        assertThat(exception.message).isEqualTo("insufficient stock")
        assertThat(exception.data).isEqualTo("상품 재고가 부족합니다. id: ${productEntity.id}")
    }

    @Test
    fun `주문 내역을 상세조회 할 수 있다`() {
        val productEntities = generateProductEntities()
        val checkoutItems = generateCheckoutItems(productEntities)
        val command = generateCheckout(checkoutItems)
        val orderEntity = generateOrderEntity(command)
        val orderItemEntities = checkoutItems.mapIndexed { index, item ->
            OrderItemEntity(
                orderId = orderEntity.id,
                productId = item.productId,
                productName = productEntities[index].name,
                quantity = item.quantity,
                discountPrice = DiscountPrice(item.discountAmount),
                price = Price(item.amount),
                totalPrice = TotalPrice(item.totalAmount)
            )
        }
        every { reader.read(any()) } returns orderEntity
        every { orderItemReader.readAll(any()) } returns orderItemEntities

        val order = service.getOrder(orderEntity.id)

        assertThat(order.orderCode).isEqualTo(orderEntity.orderCode)
        assertThat(order.status).isEqualTo(orderEntity.status)
        assertThat(order.userId).isEqualTo(orderEntity.userId)
        assertThat(order.orderer.name).isEqualTo(orderEntity.orderer.name)
        assertThat(order.orderer.phoneNumber).isEqualTo(orderEntity.orderer.phoneNumber)
        assertThat(order.deliveryDetailData.address).isEqualTo(orderEntity.deliveryDetail.address)
        assertThat(order.deliveryDetailData.addressDetail).isEqualTo(orderEntity.deliveryDetail.addressDetail)
        assertThat(order.deliveryDetailData.recipientName).isEqualTo(orderEntity.deliveryDetail.recipientName)
        assertThat(order.deliveryDetailData.message).isEqualTo(orderEntity.deliveryDetail.message)
        assertThat(order.totalProductPrice).isEqualTo(orderEntity.totalProductPrice)
        assertThat(order.totalDiscountPrice).isEqualTo(orderEntity.totalDiscountPrice)
        assertThat(order.shippingFee).isEqualTo(orderEntity.shippingFee)
        assertThat(order.totalPrice).isEqualTo(orderEntity.totalPrice)
        assertThat(order.items).hasSize(2)
        order.items.forEachIndexed { index, item ->
            assertThat(item.orderId).isEqualTo(orderItemEntities[index].orderId)
            assertThat(item.productName).isEqualTo(orderItemEntities[index].productName)
            assertThat(item.quantity).isEqualTo(orderItemEntities[index].quantity)
            assertThat(item.discountPrice).isEqualTo(orderItemEntities[index].discountPrice)
            assertThat(item.price).isEqualTo(orderItemEntities[index].price)
            assertThat(item.totalPrice).isEqualTo(orderItemEntities[index].totalPrice)
        }
    }

    @Test
    fun `존재하지 않는 주문을 상세조회 할 수 없다`() {
        val invalidId = 9L
        every { reader.read(any()) } throws OrderNotFoundException("주문을 찾을 수 없습니다. id: $invalidId")

        val exception = assertThrows<OrderNotFoundException> {
            service.getOrder(invalidId)
        }

        assertThat(exception.message).isEqualTo("order notFound")
        assertThat(exception.data).isEqualTo("주문을 찾을 수 없습니다. id: $invalidId")
    }

    private fun generateOrderEntity(command: Checkout) = OrderEntity(
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
        totalProductPrice = TotalProductPrice(command.totalProductAmount),
        totalDiscountPrice = TotalDiscountPrice(command.totalDiscountAmount),
        shippingFee = command.shippingFee,
        totalPrice = TotalPrice(command.totalAmount),
    )

    private fun generateCheckout(checkoutItems: List<CheckoutItem>): Checkout {
        val totalProductPrice = checkoutItems[0].amount + checkoutItems[1].amount
        val totalDiscountAmount = checkoutItems[0].discountAmount + checkoutItems[1].discountAmount
        val shippingFee = 3000L
        val totalPrice = totalProductPrice - totalDiscountAmount + shippingFee

        return Checkout(
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
    }

    private fun generateCheckoutItems(productEntities: List<ProductEntity>) =
        listOf(
            CheckoutItem(
                productId = productEntities[0].id,
                quantity = 1,
                discountAmount = productEntities[0].discountPrice.discountAmount * 1,
                amount = productEntities[0].price.amount * 1,
                totalAmount = productEntities[0].calculateTotalPrice() * 1,
            ),
            CheckoutItem(
                productId = productEntities[1].id,
                quantity = 2,
                discountAmount = productEntities[1].discountPrice.discountAmount * 2,
                amount = productEntities[1].price.amount * 2,
                totalAmount = productEntities[1].calculateTotalPrice() * 2,
            ),
        )

    private fun generateProductEntities() = listOf(
        ProductEntity(
            1,
            ON_SALE,
            "product-1",
            "thumbnail-1.png",
            com.hyuuny.ecommerce.storage.db.core.catalog.products.Price(20000),
            com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice(1000),
            StockQuantity(100)
        ),
        ProductEntity(
            2,
            ON_SALE,
            "product-2",
            "thumbnail-2.png",
            com.hyuuny.ecommerce.storage.db.core.catalog.products.Price(20000),
            com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice(2000),
            StockQuantity(200)
        ),
    )
}