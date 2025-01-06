package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity

data class BrandData(
    val id: Long,
    val nameKo: String,
    val nameEn: String,
) {
    constructor(brandEntity: BrandEntity) : this(
        id = brandEntity.id,
        nameKo = brandEntity.nameKo,
        nameEn = brandEntity.nameEn,
    )
}

data class BrandView(
    val id: Long,
    val nameKo: String,
    val nameEn: String,
    val bannerImageUrl: String,
) {
    constructor(brandEntity: BrandEntity) : this(
        id = brandEntity.id,
        nameKo = brandEntity.nameKo,
        nameEn = brandEntity.nameEn,
        bannerImageUrl = brandEntity.bannerImageUrl,
    )
}
