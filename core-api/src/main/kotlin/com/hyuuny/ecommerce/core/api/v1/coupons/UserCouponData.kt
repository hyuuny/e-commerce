package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.*
import java.time.LocalDate
import java.time.LocalDateTime

class UserCouponData(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val couponType: CouponType,
    val couponName: String,
    val publishedDate: LocalDate,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val minimumOrderPrice: MinimumOrderPrice,
    val maximumDiscountPrice: MaximumDiscountPrice?,
    val used: Boolean,
    val usedDateTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(entity: UserCouponEntity, coupon: CouponEntity) : this(
        id = entity.id,
        userId = entity.userId,
        couponId = entity.couponId,
        couponType = coupon.couponType,
        couponName = coupon.name,
        publishedDate = entity.publishedDate,
        fromDate = coupon.fromDate,
        toDate = coupon.toDate,
        minimumOrderPrice = coupon.minimumOrderPrice,
        maximumDiscountPrice = coupon.maximumDiscountPrice,
        used = entity.used,
        usedDateTime = entity.usedDateTime,
        createdAt = entity.createdAt,
    )
}