package com.hyuuny.ecommerce.storage.db.core.reviews

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface ReviewRepositoryCustom {
    fun findAllBySearch(search: SearchReview, pageable: Pageable): SimplePage<ReviewEntity>
    fun findReviewStatsByProductId(productId: Long): ReviewStats
}
