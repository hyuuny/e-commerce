package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.error.OrderNotFoundException
import com.hyuuny.ecommerce.storage.db.core.orders.OrderEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderReader(
    private val repository: OrderRepository,
) {
    fun read(id: Long): OrderEntity = repository.findByIdOrNull(id)
        ?: throw OrderNotFoundException("주문을 찾을 수 없습니다. id: $id")

    fun readAll(userId: Long): List<OrderEntity> = repository.findAllByUserId(userId)

    fun search(command: OrderSearchCommand, pageable: Pageable): SimplePage<OrderEntity> =
        repository.findAllBySearch(command.toSearch(), pageable)
}
