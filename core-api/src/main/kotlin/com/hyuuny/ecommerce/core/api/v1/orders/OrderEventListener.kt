package com.hyuuny.ecommerce.core.api.v1.orders

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderEventListener(
    private val orderReader: OrderReader,
    private val orderItemReader: OrderItemReader,
) {
    @Async
    @TransactionalEventListener
    fun handle(event: OrderItemCancelEvent) {
        val orderId = event.orderId
        val order = orderReader.read(orderId)
        val orderItems = orderItemReader.readAll(order.id)

        val itemCount = orderItems.count()
        val cancelCount = orderItems.count { it.isCanceled() }
        when {
            itemCount == cancelCount -> order.allCanceled()
            cancelCount > 0 -> order.partialCanceled()
        }
    }
}
