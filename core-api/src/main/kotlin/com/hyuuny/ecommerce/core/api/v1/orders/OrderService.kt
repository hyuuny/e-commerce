package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderWriter: OrderWriter,
    private val orderItemWriter: OrderItemWriter,
    private val orderReader: OrderReader,
    private val orderItemReader: OrderItemReader,
    private val productReader: ProductReader,
    private val eventPublisher: ApplicationEventPublisher,
) {
    @Transactional
    fun checkout(command: Checkout): OrderView {
        val newOrder = orderWriter.checkout(command)
        val productIds = command.items.map { it.productId }
        val productMap = productReader.readAllByIds(productIds).associateBy { it.id }
        val newOrderItems = orderItemWriter.append(command.items, newOrder, productMap)
        return OrderView(newOrder, newOrderItems)
    }

    fun getOrder(id: Long): OrderView {
        val order = orderReader.read(id)
        val orderItems = orderItemReader.readAll(order.id)
        return OrderView(order, orderItems)
    }

    fun search(command: OrderSearchCommand, pageable: Pageable): SimplePage<OrderData> {
        val page = orderReader.search(command, pageable)
        val orderIds = page.content.map { it.id }
        val orderGroup = orderItemReader.readAllByOrderIds(orderIds).groupBy { it.orderId }
        return SimplePage(page.content.mapNotNull {
            val items = orderGroup[it.id] ?: return@mapNotNull null
            OrderData(it, items)
        }, page)
    }

    @Transactional
    fun confirmPurchase(orderId: Long, orderItemId: Long) {
        val order = orderReader.read(orderId)
        val orderItem = orderItemReader.read(orderItemId, order.id)
        orderItemWriter.confirmPurchase(orderItem)
    }

    @Transactional
    fun cancel(id: Long, orderItemId: Long) {
        val order = orderReader.read(id)
        val orderItem = orderItemReader.read(orderItemId, order.id)
        orderItemWriter.cancel(orderItem)
        eventPublisher.publishEvent(OrderItemCancelEvent(order.id))
    }

    fun getAllOrders(userId: Long): List<OrderSheetData> {
        val orders = orderReader.readAll(userId)
        return orders.map { OrderSheetData(it) }
    }
}
