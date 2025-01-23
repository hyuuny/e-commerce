package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoRepository
import org.springframework.stereotype.Component

@Component
class ReviewPhotoWriter(
    private val repository: ReviewPhotoRepository,
) {
    fun write(newReviewPhotos: List<ReviewPhotoEntity>): List<ReviewPhotoEntity> = repository.saveAll(newReviewPhotos)
}
