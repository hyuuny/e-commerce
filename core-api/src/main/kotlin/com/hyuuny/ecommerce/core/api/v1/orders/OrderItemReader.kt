package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemRepository
import org.springframework.stereotype.Component

@Component
class OrderItemReader(
    private val repository: OrderItemRepository,
) {
    fun readAll(orderId: Long): List<OrderItemEntity> = repository.findAllByOrderId(orderId)

    fun readAllByOrderIds(ids: List<Long>): List<OrderItemEntity> = repository.findAllById(ids)
}
