package com.hyuuny.ecommerce.storage.db.core.reviews

data class SearchReview(
    val productId: Long? = null,
    val userId: Long? = null,
    val type: ReviewType? = null,
)

data class ReviewStats(
    val averageScore: Double,
    val reviewCount: Long
)
