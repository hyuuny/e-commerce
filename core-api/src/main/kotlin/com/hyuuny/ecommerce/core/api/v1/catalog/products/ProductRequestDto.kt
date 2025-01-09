package com.hyuuny.ecommerce.core.api.v1.catalog.products

data class ProductSearchRequestDto(
    val categoryId: Long? = null,
    val brandId: Long? = null,
    val name: String? = null,
) {
    fun toCommand(): ProductSearchCommand = ProductSearchCommand(
        categoryId = categoryId,
        brandId = brandId,
        name = name,
    )
}
