package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.products.SearchProduct

data class ProductSearchCommand(
    val categoryId: Long? = null,
    val brandId: Long? = null,
    val name: String? = null,
) {
    fun toSearch(): SearchProduct = SearchProduct(
        categoryId = categoryId,
        brandId = brandId,
        name = name
    )
}
