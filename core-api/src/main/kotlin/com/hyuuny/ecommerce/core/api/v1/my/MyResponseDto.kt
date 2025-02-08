package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus

data class MyLikedProductResponseDto(
    val productId: Long,
    val brandId: Long,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Long,
    val discountPrice: Long,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: Long,
    val isLiked: Boolean,
    val badges: List<MyLikedProductBadgeResponseDto>,
) {
    constructor(data: MyLikedProductData) : this(
        productId = data.productId,
        brandId = data.brandId,
        status = data.status,
        name = data.name,
        thumbnailUrl = data.thumbnailUrl,
        price = data.price.amount,
        discountPrice = data.discountPrice.discountAmount,
        discountPercent = data.discountPercent,
        totalPrice = data.totalPrice,
        stockQuantity = data.stockQuantity.quantity,
        isLiked = data.isLiked,
        badges = data.badges.map { MyLikedProductBadgeResponseDto(it) },
    )
}

data class MyLikedProductBadgeResponseDto(
    val productId: Long,
    val title: String,
    val color: String,
    val bgColor: String,
) {
    constructor(data: MyLikedProductBadgeData) : this(
        productId = data.productId,
        title = data.title,
        color = data.color,
        bgColor = data.bgColor,
    )
}
