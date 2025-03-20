package com.hyuuny.ecommerce.core.api.v1.my

data class UserCouponSearchReqeustDto(
    val userId: Long,
    val used: Boolean,
) {
    fun toCommand(): UserCouponSearchCommand = UserCouponSearchCommand(
        userId = userId,
        used = used,
    )
}