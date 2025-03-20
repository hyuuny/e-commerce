package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.storage.db.core.coupons.SearchUserCoupon

data class UserCouponSearchCommand(
    val userId: Long,
    val used: Boolean,
) {
    fun toSearch(): SearchUserCoupon = SearchUserCoupon(
        userId = userId,
        used = used,
    )
}