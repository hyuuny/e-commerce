package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
import java.time.LocalDateTime

data class ReviewViewResponseDto(
    val id: Long,
    val type: ReviewType,
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Int,
    val photos: List<ReviewPhotoResponseDto>,
    val createdAt: LocalDateTime,
) {
    constructor(reviewViewData: ReviewViewData) : this(
        id = reviewViewData.id,
        type = reviewViewData.type,
        userId = reviewViewData.userId,
        orderItemId = reviewViewData.orderItemId,
        productId = reviewViewData.productId,
        content = reviewViewData.content,
        score = reviewViewData.score.score,
        photos = reviewViewData.photos.map { ReviewPhotoResponseDto(it) },
        createdAt = reviewViewData.createdAt,
    )
}

data class ReviewPhotoResponseDto(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
) {
    constructor(reviewPhotoViewData: ReviewPhotoViewData) : this(
        id = reviewPhotoViewData.id,
        reviewId = reviewPhotoViewData.reviewId,
        photoUrl = reviewPhotoViewData.photoUrl,
    )
}
