package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.error.InsufficientStockException
import com.hyuuny.ecommerce.core.support.error.ProductNotFoundException
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemEntity
import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemRepository
import org.springframework.stereotype.Component

@Component
class OrderItemWriter(
    private val repository: OrderItemRepository
) {
    fun append(
        checkoutItems: List<CheckoutItem>,
        order: OrderEntity,
        productMap: Map<Long, ProductEntity>,
    ): List<OrderItemEntity> {
        val orderItemEntities = checkoutItems.map { toOrderItemEntity(it, order, productMap.getOrThrow(it.productId)) }
        return repository.saveAll(orderItemEntities)
    }

    private fun toOrderItemEntity(item: CheckoutItem, order: OrderEntity, product: ProductEntity): OrderItemEntity {
        if (product.isInsufficientStock(item.quantity)) {
            throw InsufficientStockException("상품 재고가 부족합니다. id: ${product.id}")
        }
        product.decrease(item.quantity)
        return item.toEntity(order.id, product.name)
    }

    private fun Map<Long, ProductEntity>.getOrThrow(productId: Long): ProductEntity =
        this[productId] ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $productId")
}
