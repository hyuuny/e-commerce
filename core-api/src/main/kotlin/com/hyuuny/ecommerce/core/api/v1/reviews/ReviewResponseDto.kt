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
    constructor(viewData: ReviewViewData) : this(
        id = viewData.id,
        type = viewData.type,
        userId = viewData.userId,
        orderItemId = viewData.orderItemId,
        productId = viewData.productId,
        content = viewData.content,
        score = viewData.score.score,
        photos = viewData.photos.map { ReviewPhotoResponseDto(it) },
        createdAt = viewData.createdAt,
    )
}

data class ReviewPhotoResponseDto(
    val id: Long,
    val reviewId: Long,
    val photoUrl: String,
) {
    constructor(data: ReviewPhotoData) : this(
        id = data.id,
        reviewId = data.reviewId,
        photoUrl = data.photoUrl,
    )
}

data class ReviewResponseDto(
    val id: Long,
    val type: ReviewType,
    val userId: Long,
    val userName: String,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Int,
    val photos: List<ReviewPhotoResponseDto>,
    val createdAt: LocalDateTime,
) {
    constructor(data: ReviewData) : this(
        id = data.id,
        type = data.type,
        userId = data.userId,
        userName = data.userName,
        orderItemId = data.orderItemId,
        productId = data.productId,
        content = data.content,
        score = data.score.score,
        photos = data.photos.map { ReviewPhotoResponseDto(it) },
        createdAt = data.createdAt,
    )
}

data class ReviewStatsResponseDto(
    val averageScore: Double,
    val reviewCount: Long
) {
    constructor(data: ReviewStatsData) : this(
        averageScore = data.averageScore,
        reviewCount = data.reviewCount,
    )
}
