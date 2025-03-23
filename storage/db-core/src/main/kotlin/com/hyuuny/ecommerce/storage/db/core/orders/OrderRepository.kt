package com.hyuuny.ecommerce.storage.db.core.orders

import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Long>, OrderRepositoryCustom {
    fun findAllByUserId(userId: Long) : List<OrderEntity>
}
