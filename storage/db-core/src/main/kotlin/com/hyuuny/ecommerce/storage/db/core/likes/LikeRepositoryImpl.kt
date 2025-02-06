package com.hyuuny.ecommerce.storage.db.core.likes

import com.hyuuny.ecommerce.storage.db.core.likes.QLikeEntity.likeEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class LikeRepositoryImpl : LikeRepositoryCustom, QuerydslRepositorySupport(LikeEntity::class.java) {
    override fun findAllByUserId(userId: Long, pageable: Pageable): SimplePage<LikeEntity> {
        val query = from(likeEntity)
            .where(likeEntity.userId.eq(userId))
            .orderBy(*QueryDslUtil.getSort(pageable, likeEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()

        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}
