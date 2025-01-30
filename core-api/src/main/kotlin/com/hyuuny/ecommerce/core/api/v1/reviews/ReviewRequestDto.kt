package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType

data class WriteReviewRequestDto(
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    val content: String,
    val score: Int,
    val photos: List<WriteReviewPhotoRequestDto>,
) {
    fun toWriteReview() = WriteReview(
        userId = userId,
        orderItemId = orderItemId,
        productId = productId,
        content = content,
        score = score,
        photos = photos.map { it.toWriteReviewPhoto() },
    )
}

data class WriteReviewPhotoRequestDto(
    val photoUrl: String,
) {
    fun toWriteReviewPhoto() = WriteReviewPhoto(
        photoUrl = photoUrl,
    )
}

data class ReviewSearchRequestDto(
    val productId: Long? = null,
    val userId: Long? = null,
    val type: ReviewType? = null,
){
    fun toCommand(): ReviewSearchCommand = ReviewSearchCommand(
        productId =  productId,
        userId = userId,
        type = type
    )
}
