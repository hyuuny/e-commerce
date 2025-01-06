package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.storage.db.core.brands.SearchBrand

data class BrandSearchCommand(
    val nameKo: String? = null,
    val nameEn: String? = null,
) {
    fun toSearch(): SearchBrand = SearchBrand(
        nameKo = nameKo,
        nameEn = nameEn,
    )
}
