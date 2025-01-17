package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.error.OrderItemNotFoundException
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemRepository
import org.springframework.stereotype.Component

@Component
class OrderItemReader(
    private val repository: OrderItemRepository,
) {
    fun readAll(orderId: Long): List<OrderItemEntity> = repository.findAllByOrderId(orderId)
    
    fun readAllByOrderIds(orderIds: List<Long>): List<OrderItemEntity> = repository.findAllByOrderIdIn(orderIds)

    fun read(id: Long, orderId: Long): OrderItemEntity = repository.findByIdAndOrderId(id, orderId)
        ?: throw OrderItemNotFoundException("주문 상품을 찾을 수 없습니다. id: $id, orderId: $orderId")
}
