package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.CouponRepository
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponRepository
import org.springframework.stereotype.Component

@Component
class CouponWriter(
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
) {
    fun write(newUserCoupon: UserCouponEntity): UserCouponEntity = userCouponRepository.save(newUserCoupon)

    fun incrementIssuedCount(id: Long) {
        couponRepository.incrementIssuedCount(id)
    }
}