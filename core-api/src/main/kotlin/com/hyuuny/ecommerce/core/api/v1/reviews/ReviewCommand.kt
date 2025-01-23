package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
import com.hyuuny.ecommerce.storage.db.core.reviews.Score

data class WriteReview(
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Int,
    val photos: List<WriteReviewPhoto>,
) {
    fun toEntity(): ReviewEntity = ReviewEntity(
        type = if (photos.isEmpty()) ReviewType.TEXT else ReviewType.PHOTO,
        userId = userId,
        orderItemId = orderItemId,
        productId = productId,
        content = content,
        score = Score(score),
    )
}

data class WriteReviewPhoto(
    val photoUrl: String,
) {
    fun toEntity(reviewId: Long) = ReviewPhotoEntity(
        reviewId = reviewId,
        photoUrl = photoUrl,
    )
}
