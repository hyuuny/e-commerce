package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.api.v1.my.UserCouponSearchCommand
import com.hyuuny.ecommerce.core.support.error.UserCouponNotFoundException
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UserCouponReader(
    private val repository: UserCouponRepository,
) {
    fun read(userId: Long, couponId: Long): UserCouponEntity = repository.findByUserIdAndCouponId(userId, couponId)
        ?: throw UserCouponNotFoundException("$userId 번 회원의 $couponId 번 쿠폰을 찾을 수 없습니다.")

    fun search(command: UserCouponSearchCommand, pageable: Pageable): SimplePage<UserCouponEntity> =
        repository.findAllByUserId(command.toSearch(), pageable)
}