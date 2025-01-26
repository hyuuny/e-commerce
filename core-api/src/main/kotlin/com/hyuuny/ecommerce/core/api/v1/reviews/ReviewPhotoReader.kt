package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoRepository
import org.springframework.stereotype.Component

@Component
class ReviewPhotoReader(
    private val repository: ReviewPhotoRepository,
) {
    fun readAll(reviewId: Long): List<ReviewPhotoEntity> = repository.findAllByReviewId(reviewId)
}
