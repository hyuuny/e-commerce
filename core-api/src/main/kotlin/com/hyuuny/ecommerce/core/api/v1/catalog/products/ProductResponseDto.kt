package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus

data class ProductResponseDto(
    val id: Long,
    val brandId: Long,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Long,
    val discountPrice: Long,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: Long,
    val badges: List<ProductBadgeResponseDto>,
) {
    constructor(data: ProductData) : this(
        id = data.id,
        brandId = data.brandId,
        status = data.status,
        name = data.name,
        thumbnailUrl = data.thumbnailUrl,
        price = data.price.amount,
        discountPrice = data.discountPrice.discountAmount,
        discountPercent = data.discountPercent,
        totalPrice = data.totalPrice,
        stockQuantity = data.stockQuantity.quantity,
        badges = data.badges.map { ProductBadgeResponseDto(it) },
    )
}

data class ProductViewResponseDto(
    val id: Long,
    val brandId: Long,
    val brandName: String,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Long,
    val discountPrice: Long,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: Long,
    val banners: List<ProductBannerResponseDto>,
    val contents: List<ProductContentResponseDto>,
    val badges: List<ProductBadgeResponseDto>,
) {
    constructor(productView: ProductView) : this(
        id = productView.id,
        brandId = productView.brandId,
        brandName = productView.brandName,
        status = productView.status,
        name = productView.name,
        thumbnailUrl = productView.thumbnailUrl,
        price = productView.price.amount,
        discountPrice = productView.discountPrice.discountAmount,
        discountPercent = productView.discountPercent,
        totalPrice = productView.totalPrice,
        stockQuantity = productView.stockQuantity.quantity,
        banners = productView.banners.map { ProductBannerResponseDto(it) },
        contents = productView.contents.map { ProductContentResponseDto(it) },
        badges = productView.badges.map { ProductBadgeResponseDto(it) },
    )
}

data class ProductBannerResponseDto(
    val productId: Long,
    val imageUrl: String,
) {
    constructor(productBannerData: ProductBannerData) : this(
        productId = productBannerData.productId,
        imageUrl = productBannerData.imageUrl,
    )
}

data class ProductContentResponseDto(
    val productId: Long,
    val imageUrl: String,
) {
    constructor(productContentData: ProductContentData) : this(
        productId = productContentData.productId,
        imageUrl = productContentData.imageUrl,
    )
}

data class ProductBadgeResponseDto(
    val productId: Long,
    val title: String,
    val color: String,
    val bgColor: String,
) {
    constructor(productBadgeData: ProductBadgeData) : this(
        productId = productBadgeData.productId,
        title = productBadgeData.title,
        color = productBadgeData.color,
        bgColor = productBadgeData.bgColor,
    )
}

