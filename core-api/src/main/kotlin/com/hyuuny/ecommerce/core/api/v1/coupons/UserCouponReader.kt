package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.api.v1.my.UserCouponSearchCommand
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class UserCouponReader(
    private val repository: UserCouponRepository,
) {
    fun search(command: UserCouponSearchCommand, pageable: Pageable): SimplePage<UserCouponEntity> =
        repository.findAllByUserId(command.toSearch(), pageable)
}