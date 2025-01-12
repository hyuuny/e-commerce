package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.core.support.error.CheckoutTimeoutException
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.ON_SALE
import com.hyuuny.ecommerce.storage.db.core.catalog.products.StockQuantity
import com.hyuuny.ecommerce.storage.db.core.orders.*
import com.hyuuny.ecommerce.storage.db.core.utils.CodeGenerator
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class OrderServiceTest {
    private lateinit var writer: OrderWriter
    private lateinit var orderItemWriter: OrderItemWriter
    private lateinit var productReader: ProductReader
    private lateinit var redissonClient: RedissonClient
    private lateinit var service: OrderService

    private val tryLockTime = 10L

    @BeforeEach
    fun setUp() {
        writer = mockk()
        orderItemWriter = mockk()
        productReader = mockk()
        redissonClient = mockk()
        service = OrderService(writer, orderItemWriter, productReader, redissonClient)
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
                discountPrice = DiscountPrice(item.discountPrice),
                price = Price(item.price),
                totalPrice = TotalPrice(item.totalPrice)
            )
        }
        val locks = listOf(mockk<RLock>(), mockk<RLock>())
        every { redissonClient.getLock(ofType<String>()) } answers { locks[0] } andThenAnswer { locks[1] }
        every { locks[0].tryLock(tryLockTime, TimeUnit.SECONDS) } returns true
        every { locks[1].tryLock(tryLockTime, TimeUnit.SECONDS) } returns true
        every { productReader.readAllByIds(any()) } returns productEntities
        every { writer.checkout(any()) } returns orderEntity
        every { orderItemWriter.append(any(), any(), any()) } returns orderItemEntities
        every { locks.forEach { it.unlock() } } just Runs

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
        assertThat(newOrder.totalDiscountAmount).isEqualTo(orderEntity.totalDiscountAmount)
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
    fun `주문시 제품 잠금 획득에 실패하면 주문을 할 수 없다`() {
        val productEntities = generateProductEntities()
        val checkoutItems = generateCheckoutItems(productEntities)
        val command = generateCheckout(checkoutItems)

        val locks = listOf(mockk<RLock>(), mockk<RLock>())
        every { locks[0].name } returns "$tryLockTime:0"
        every { locks[1].name } returns "$tryLockTime:1"
        every { redissonClient.getLock(ofType<String>()) } answers { locks[0] } andThenAnswer { locks[1] }
        every { locks[0].tryLock(tryLockTime, TimeUnit.SECONDS) } returns false
        every { locks[1].tryLock(tryLockTime, TimeUnit.SECONDS) } returns true
        every { locks[0].unlock() } just Runs
        every { locks[1].unlock() } just Runs

        val exception = org.junit.jupiter.api.assertThrows<CheckoutTimeoutException> {
            service.checkout(command)
        }

        assertThat(exception.message).isEqualTo("checkout timeout")
        assertThat(exception.data).isEqualTo("제품에 대한 잠금을 획득할 수 없습니다. name: $tryLockTime:0")
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
        totalProductPrice = TotalProductPrice(command.totalProductPrice),
        totalDiscountAmount = TotalDiscountPrice(command.totalDiscountAmount),
        shippingFee = command.shippingFee,
        totalPrice = TotalPrice(command.totalPrice),
    )

    private fun generateCheckout(checkoutItems: List<CheckoutItem>): Checkout {
        val totalProductPrice = checkoutItems[0].price + checkoutItems[1].price
        val totalDiscountAmount = checkoutItems[0].discountPrice + checkoutItems[1].discountPrice
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
            totalProductPrice = totalProductPrice,
            totalDiscountAmount = totalDiscountAmount,
            shippingFee = shippingFee,
            totalPrice = totalPrice,
            items = checkoutItems
        )
    }

    private fun generateCheckoutItems(productEntities: List<ProductEntity>) =
        listOf(
            CheckoutItem(
                productId = productEntities[0].id,
                quantity = 1,
                discountPrice = productEntities[0].discountPrice.discountAmount * 1,
                price = productEntities[0].price.amount * 1,
                totalPrice = productEntities[0].calculateTotalPrice() * 1,
            ),
            CheckoutItem(
                productId = productEntities[1].id,
                quantity = 2,
                discountPrice = productEntities[1].discountPrice.discountAmount * 2,
                price = productEntities[1].price.amount * 2,
                totalPrice = productEntities[1].calculateTotalPrice() * 2,
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