package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.OrderEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderRepository
import com.hyuuny.ecommerce.storage.db.core.utils.CodeGenerator
import org.springframework.stereotype.Component

@Component
class OrderWriter(
    private val repository: OrderRepository,
) {
    fun checkout(checkout: Checkout): OrderEntity {
        val orderCode = CodeGenerator.generateOrderCode()
        val newOrder = checkout.toEntity(orderCode)
        return repository.save(newOrder)
    }
}
