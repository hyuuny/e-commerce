package com.hyuuny.ecommerce.core.api.v1.reviews

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ReviewService(
    private val reviewWriter: ReviewWriter,
    private val reviewPhotoWriter: ReviewPhotoWriter,
    private val validator: OrderReviewValidator,
) {
    @Transactional
    fun write(writeReview: WriteReview): ReviewViewData {
        validator.validate(writeReview.orderItemId, writeReview.productId)
        val newReview = reviewWriter.write(writeReview.toEntity())
        val newReviewPhotos = reviewPhotoWriter.write(writeReview.photos.map { it.toEntity(newReview.id) })
        return ReviewViewData(newReview, newReviewPhotos)
    }
}
