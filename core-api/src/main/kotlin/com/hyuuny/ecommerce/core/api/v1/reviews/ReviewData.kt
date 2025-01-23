package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
import com.hyuuny.ecommerce.storage.db.core.reviews.Score
import java.time.LocalDateTime

data class ReviewViewData(
    val id: Long,
    val type: ReviewType,
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Score,
    val photos: List<ReviewPhotoViewData>,
    val createdAt: LocalDateTime,
) {
    constructor(reviewEntity: ReviewEntity, reviewPhotoEntities: List<ReviewPhotoEntity>) : this(
        id = reviewEntity.id,
        type = reviewEntity.type,
        userId = reviewEntity.userId,
        orderItemId = reviewEntity.orderItemId,
        productId = reviewEntity.productId,
        content = reviewEntity.content,
        score = reviewEntity.score,
        photos = reviewPhotoEntities.map { ReviewPhotoViewData(it) },
        createdAt = reviewEntity.createdAt,
    )
}

data class ReviewPhotoViewData(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
) {
    constructor(reviewPhotoEntity: ReviewPhotoEntity) : this(
        id = reviewPhotoEntity.id,
        reviewId = reviewPhotoEntity.reviewId,
        photoUrl = reviewPhotoEntity.photoUrl,
    )
}
