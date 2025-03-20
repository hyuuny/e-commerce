package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.support.error.CouponNotFoundException
import com.hyuuny.ecommerce.storage.db.core.coupons.CouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.CouponRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CouponReader(
    private val couponRepository: CouponRepository,
) {
    fun read(id: Long): CouponEntity = couponRepository.findByIdOrNull(id)
        ?: throw CouponNotFoundException("쿠폰을 찾을 수 없습니다. id: $id")
}