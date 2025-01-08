package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductBadgeEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductBadgeRepository
import org.springframework.stereotype.Component

@Component
class ProductBadgeReader(
    private val repository: ProductBadgeRepository,
) {
    fun readAll(productId: Long): List<ProductBadgeEntity> = repository.findAllByProductId(productId)
    fun readAllByIds(productIds: List<Long>): List<ProductBadgeEntity> = repository.findAllByProductIdIn(productIds)
}