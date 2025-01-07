package com.hyuuny.ecommerce.core.api.v1.brands

data class BrandResponseDto(
    val id: Long,
    val nameKo: String,
    val nameEn: String,
) {
    constructor(brandData: BrandData) : this(
        id = brandData.id,
        nameKo = brandData.nameKo,
        nameEn = brandData.nameEn,
    )
}

data class BrandViewResponseDto(
    val id: Long,
    val nameKo: String,
    val nameEn: String,
    val bannerImageUrl: String,
) {
    constructor(brandView: BrandView) : this(
        id = brandView.id,
        nameKo = brandView.nameKo,
        nameEn = brandView.nameEn,
        bannerImageUrl = brandView.bannerImageUrl,
    )
}
