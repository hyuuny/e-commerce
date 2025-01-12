package com.hyuuny.ecommerce.storage.db.core.orders

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository : JpaRepository<OrderItemEntity, Long> {
}
