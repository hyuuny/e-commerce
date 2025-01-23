package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewRepository
import org.springframework.stereotype.Component

@Component
class ReviewWriter(
    private val repository: ReviewRepository,
) {
    fun write(newReview: ReviewEntity) = repository.save(newReview)
}
