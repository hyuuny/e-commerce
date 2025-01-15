package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
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
}
