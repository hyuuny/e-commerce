package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductBannerEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductBannerRepository
import org.springframework.stereotype.Component

@Component
class ProductBannerReader(
    private val repository: ProductBannerRepository
) {
    fun readAll(productId: Long): List<ProductBannerEntity> = repository.findAllByProductId(productId)
}
