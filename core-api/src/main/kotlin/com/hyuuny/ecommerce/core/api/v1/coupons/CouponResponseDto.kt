package com.hyuuny.ecommerce.core.api.v1.coupons

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