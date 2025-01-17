package com.hyuuny.ecommerce.storage.db.core.orders

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItemEntity, Long> {
    fun findAllByOrderId(orderId: Long): List<OrderItemEntity>
    fun findAllByOrderIdIn(orderIds: List<Long>): List<OrderItemEntity>
    fun findByIdAndOrderId(id: Long, orderId: Long): OrderItemEntity?
}
