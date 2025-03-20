package com.hyuuny.ecommerce.storage.db.core.coupons

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface UserCouponRepositoryCustom {
    fun findAllByUserId(search: SearchUserCoupon, pageable: Pageable): SimplePage<UserCouponEntity>
}