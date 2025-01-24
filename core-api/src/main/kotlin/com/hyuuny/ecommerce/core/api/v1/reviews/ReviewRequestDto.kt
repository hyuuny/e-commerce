package com.hyuuny.ecommerce.core.api.v1.reviews

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
