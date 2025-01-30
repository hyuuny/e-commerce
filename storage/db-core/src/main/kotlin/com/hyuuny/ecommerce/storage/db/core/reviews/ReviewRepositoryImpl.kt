package com.hyuuny.ecommerce.storage.db.core.reviews

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.reviews.QReviewEntity.reviewEntity
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class ReviewRepositoryImpl : ReviewRepositoryCustom, QuerydslRepositorySupport(ReviewEntity::class.java) {
    override fun findAllBySearch(search: SearchReview, pageable: Pageable): SimplePage<ReviewEntity> {
        var query = from(reviewEntity)

        with(search) {
            productId?.let { query = query.where(reviewEntity.productId.eq(it)) }
            userId?.let { query = query.where(reviewEntity.userId.eq(it)) }
            type?.let { query = query.where(reviewEntity.type.eq(it)) }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, reviewEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()
        val last = content.size <= size
        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }

    override fun findReviewStatsByProductId(productId: Long): ReviewStats {
        val result = from(reviewEntity)
            .where(reviewEntity.productId.eq(productId))
            .select(
                reviewEntity.score.score.avg(),
                reviewEntity.count()
            )
            .fetchOne()

        return ReviewStats(
            averageScore = result?.get(0, Double::class.java) ?: 0.0,
            reviewCount = result?.get(1, Long::class.java) ?: 0
        )
    }
}