package com.hyuuny.ecommerce.storage.db.core.coupons

import com.hyuuny.ecommerce.storage.db.core.coupons.QUserCouponEntity.userCouponEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class UserCouponRepositoryImpl : UserCouponRepositoryCustom, QuerydslRepositorySupport(UserCouponEntity::class.java) {
    override fun findAllByUserId(search: SearchUserCoupon, pageable: Pageable): SimplePage<UserCouponEntity> {
        val query = from(userCouponEntity)
            .where(
                userCouponEntity.userId.eq(search.userId),
                userCouponEntity.used.eq(search.used),
            )
            .orderBy(*QueryDslUtil.getSort(pageable, userCouponEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()

        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}