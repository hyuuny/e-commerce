package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.*

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

data class ReviewSearchCommand(
    val productId: Long? = null,
    val userId: Long? = null,
    val type: ReviewType? = null,
) {
    fun toSearch(): SearchReview = SearchReview(
        type = type,
        productId = productId,
        userId = userId,
    )
}
