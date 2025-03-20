package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountType
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import java.time.LocalDate

data class IssueUserCoupon(
    val userId: Long,
    val couponId: Long,
    val discountType: DiscountType,
) {
    fun toEntity(now: LocalDate, expiredDate: LocalDate): UserCouponEntity = UserCouponEntity(
        userId = userId,
        couponId = couponId,
        publishedDate = now,
        expiredDate = expiredDate,
    )
}