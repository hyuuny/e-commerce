package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.CouponType
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import java.time.LocalDate
import java.time.LocalDateTime

data class UserCouponResponseDto(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val publishedDate: LocalDate,
    val expiredDate: LocalDate,
    val used: Boolean,
    val usedDateTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(entity: UserCouponEntity) : this(
        id = entity.id,
        userId = entity.userId,
        couponId = entity.couponId,
        publishedDate = entity.publishedDate,
        expiredDate = entity.expiredDate,
        used = entity.used,
        usedDateTime = entity.usedDateTime,
        createdAt = entity.createdAt
    )
}

data class UserCouponViewResponseDto(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val couponType: CouponType,
    val couponName: String,
    val publishedDate: LocalDate,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val minimumOrderPrice: Long,
    val maximumDiscountPrice: Long?,
    val used: Boolean,
    val usedDateTime: LocalDateTime?,
    val createdAt: LocalDateTime,
) {
    constructor(data: UserCouponData) : this(
        id = data.id,
        userId = data.userId,
        couponId = data.couponId,
        couponType = data.couponType,
        couponName = data.couponName,
        publishedDate = data.publishedDate,
        fromDate = data.fromDate,
        toDate = data.toDate,
        minimumOrderPrice = data.minimumOrderPrice.minimumOrderAmount,
        maximumDiscountPrice = data.maximumDiscountPrice?.maximumDiscountAmount,
        used = data.used,
        usedDateTime = data.usedDateTime,
        createdAt = data.createdAt,
    )
}