package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.DiscountType

data class IssueUserCouponReqeustDto(
    val userId: Long,
    val couponId: Long,
    val discountType: DiscountType,
){
    fun toCommand(): IssueUserCoupon = IssueUserCoupon(
        userId = userId,
        couponId = couponId,
        discountType = discountType,
    )
}