package com.hyuuny.ecommerce.storage.db.core.catalog.products

data class SearchProduct(
    val categoryId: Long? = null,
    val brandId: Long? = null,
    val name: String? = null,
)