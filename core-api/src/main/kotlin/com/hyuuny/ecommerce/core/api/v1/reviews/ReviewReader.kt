package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.support.error.ReviewNotFoundException
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewRepository
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewStats
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ReviewReader(
    private val repository: ReviewRepository,
) {
    fun read(id: Long): ReviewEntity = repository.findByIdOrNull(id)
        ?: throw ReviewNotFoundException("후기를 찾을 수 없습니다. id: $id")

    fun search(command: ReviewSearchCommand, pageable: Pageable): SimplePage<ReviewEntity> =
        repository.findAllBySearch(command.toSearch(), pageable)

    fun readReviewStatsByProductId(productId: Long): ReviewStats = repository.findReviewStatsByProductId(productId)
}
