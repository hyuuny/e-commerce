package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.coupons.*
import java.time.LocalDate
import java.time.LocalDateTime
import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountPrice as WonCouponDiscountPrice


data class MyLikedProductData(
    val productId: Long,
    val brandId: Long,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: StockQuantity,
    val isLiked: Boolean,
    val badges: List<MyLikedProductBadgeData>,
) {
    constructor(entity: ProductEntity, badges: List<ProductBadgeEntity>, isLiked: Boolean) : this(
        productId = entity.id,
        brandId = entity.brandId,
        status = entity.status,
        name = entity.name,
        thumbnailUrl = entity.thumbnailUrl,
        price = entity.price,
        discountPrice = entity.discountPrice,
        discountPercent = entity.calculateDiscountPercent().toInt(),
        totalPrice = entity.calculateTotalPrice(),
        stockQuantity = entity.stockQuantity,
        isLiked = isLiked,
        badges = badges.map { MyLikedProductBadgeData(it) },
    )
}

data class MyLikedProductBadgeData(
    val productId: Long,
    val title: String,
    val color: String,
    val bgColor: String,
) {
    constructor(productBadgeEntity: ProductBadgeEntity) : this(
        productId = productBadgeEntity.productId,
        title = productBadgeEntity.title,
        color = productBadgeEntity.color,
        bgColor = productBadgeEntity.bgColor,
    )
}

data class UserCouponData(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val couponCode: String,
    val couponName: String,
    val minimumOrderPrice: MinimumOrderPrice,
    val maximumDiscountPrice: MaximumDiscountPrice?,
    val discountPrice: WonCouponDiscountPrice?,
    val discountPercent: DiscountPercent?,
    val publishedDate: LocalDate,
    val expiredDate: LocalDate,
    val used: Boolean,
    val usedDateTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(
        entity: UserCouponEntity,
        coupon: CouponEntity,
        discountPrice: WonCouponDiscountPrice? = null,
        discountPercent: DiscountPercent? = null
    ) : this(
        id = entity.userId,
        userId = entity.userId,
        couponId = entity.couponId,
        couponCode = coupon.code,
        couponName = coupon.name,
        minimumOrderPrice = coupon.minimumOrderPrice,
        maximumDiscountPrice = coupon.maximumDiscountPrice,
        discountPrice = discountPrice,
        discountPercent = discountPercent,
        publishedDate = entity.publishedDate,
        expiredDate = entity.expiredDate,
        used = entity.used,
        usedDateTime = entity.usedDateTime,
        createdAt = entity.createdAt,
    )
}
