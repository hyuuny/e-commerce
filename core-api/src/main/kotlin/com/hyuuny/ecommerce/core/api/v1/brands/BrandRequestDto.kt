package com.hyuuny.ecommerce.core.api.v1.brands

data class BrandSearchRequestDto(
    val nameKo: String? = null,
    val nameEn: String? = null,
) {
    fun toCommand(): BrandSearchCommand = BrandSearchCommand(
        nameKo = nameKo,
        nameEn = nameEn,
    )
}
