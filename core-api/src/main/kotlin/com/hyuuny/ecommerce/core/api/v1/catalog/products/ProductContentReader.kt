package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductContentEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductContentRepository
import org.springframework.stereotype.Component

@Component
class ProductContentReader(
    private val repository: ProductContentRepository,
) {
    fun readAll(productId: Long): List<ProductContentEntity> = repository.findAllByProductId(productId)
}
