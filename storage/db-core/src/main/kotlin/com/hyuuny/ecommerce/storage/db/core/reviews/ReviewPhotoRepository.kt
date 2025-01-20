package com.hyuuny.ecommerce.storage.db.core.reviews

import org.springframework.data.jpa.repository.JpaRepository

interface ReviewPhotoRepository : JpaRepository<ReviewPhotoEntity, Long> {
    fun findAllByReviewId(reviewId: Long): List<ReviewPhotoEntity>
}
