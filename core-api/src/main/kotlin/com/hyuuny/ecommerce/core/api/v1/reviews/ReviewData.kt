package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
import com.hyuuny.ecommerce.storage.db.core.reviews.Score
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import java.time.LocalDateTime

data class ReviewViewData(
    val id: Long,
    val type: ReviewType,
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Score,
    val photos: List<ReviewPhotoData>,
    val createdAt: LocalDateTime,
) {
    constructor(entity: ReviewEntity, reviewPhotoEntities: List<ReviewPhotoEntity>) : this(
        id = entity.id,
        type = entity.type,
        userId = entity.userId,
        orderItemId = entity.orderItemId,
        productId = entity.productId,
        content = entity.content,
        score = entity.score,
        photos = reviewPhotoEntities.map { ReviewPhotoData(it) },
        createdAt = entity.createdAt,
    )
}

data class ReviewPhotoData(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
) {
    constructor(entity: ReviewPhotoEntity) : this(
        id = entity.id,
        reviewId = entity.reviewId,
        photoUrl = entity.photoUrl,
    )
}

data class ReviewData(
    val id: Long,
    val type: ReviewType,
    val userId: Long,
    val userName: String,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Score,
    val photos: List<ReviewPhotoData>,
    val createdAt: LocalDateTime,
) {
    constructor(entity: ReviewEntity, user: UserEntity, photos: List<ReviewPhotoEntity>) : this(
        id = entity.id,
        type = entity.type,
        userId = user.id,
        userName = user.name,
        orderItemId = entity.orderItemId,
        productId = entity.productId,
        content = entity.content,
        score = entity.score,
        photos = photos.map { ReviewPhotoData(it) },
        createdAt = entity.createdAt
    )
}