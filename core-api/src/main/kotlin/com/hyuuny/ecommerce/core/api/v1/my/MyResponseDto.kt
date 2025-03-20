package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus
import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountPercent
import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountPrice
import com.hyuuny.ecommerce.storage.db.core.coupons.MaximumDiscountPrice
import com.hyuuny.ecommerce.storage.db.core.coupons.MinimumOrderPrice
import java.time.LocalDate
import java.time.LocalDateTime

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

data class UserCouponResponseDto(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val couponCode: String,
    val couponName: String,
    val minimumOrderPrice: MinimumOrderPrice,
    val maximumDiscountPrice: MaximumDiscountPrice?,
    val discountPrice: DiscountPrice?,
    val discountPercent: DiscountPercent?,
    val publishedDate: LocalDate,
    val expiredDate: LocalDate,
    val used: Boolean,
    val usedDateTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(data: UserCouponData) : this(
        id = data.id,
        userId = data.userId,
        couponId = data.couponId,
        couponCode = data.couponCode,
        couponName = data.couponName,
        minimumOrderPrice = data.minimumOrderPrice,
        maximumDiscountPrice = data.maximumDiscountPrice,
        discountPrice = data.discountPrice,
        discountPercent = data.discountPercent,
        publishedDate = data.publishedDate,
        expiredDate = data.expiredDate,
        used = data.used,
        usedDateTime = data.usedDateTime,
        createdAt = data.createdAt,
    )
}
