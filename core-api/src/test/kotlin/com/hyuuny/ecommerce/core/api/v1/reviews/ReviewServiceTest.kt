package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewPhotoEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewType
import com.hyuuny.ecommerce.storage.db.core.reviews.Score
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReviewServiceTest {
    private lateinit var reviewWriter: ReviewWriter
    private lateinit var reviewPhotoWriter: ReviewPhotoWriter
    private lateinit var validator: OrderReviewValidator
    private lateinit var reviewService: ReviewService

    @BeforeEach
    fun setUp() {
        reviewWriter = mockk()
        reviewPhotoWriter = mockk()
        validator = mockk()
        reviewService = ReviewService(reviewWriter, reviewPhotoWriter, validator)
    }

    @Test
    fun `사용자는 주문 상품에 후기를 작성할 수 있다`() {
        val reviewEntity = ReviewEntity(
            userId = 1,
            orderItemId = 1,
            productId = 1,
            type = ReviewType.PHOTO,
            content = "1. 마스크 밀착력은 평타는 하며 에센스 양도 적당해서 목부분까지 마르고도 남을양입니다.\n" +
                    "\n" +
                    "2. 평상시에 1일 1팩보다는 피부가 예민하거나 건조하실때 사용을 하시면 큰 도움이 됩니다. 각질 일어났던 부분이 진정이 됩니다.\n" +
                    "\n" +
                    "3. 자극없는 순한제품이어서 피부타입없이 누구나 사용가능합니다.\n" +
                    "\n" +
                    "결론은 피부가 건조하실때 사용을 하시면 됩니다. 피부타입에 상관없이 사용가능합니다.",
            score = Score(5),
        )
        val reviewPhotoEntities = listOf(
            ReviewPhotoEntity(reviewId = reviewEntity.id, photoUrl = "http://ecommerce.hyuuny.com/photo-1.jpg"),
            ReviewPhotoEntity(reviewId = reviewEntity.id, photoUrl = "http://ecommerce.hyuuny.com/photo-2.jpg"),
            ReviewPhotoEntity(reviewId = reviewEntity.id, photoUrl = "http://ecommerce.hyuuny.com/photo-3.jpg"),
        )
        val writeReview = WriteReview(
            userId = 1,
            orderItemId = 1,
            productId = 1,
            content = reviewEntity.content,
            score = 5,
            photos = reviewPhotoEntities.map { WriteReviewPhoto(it.photoUrl) },
        )
        every { validator.validate(any(), any()) } just Runs
        every { reviewWriter.write(any()) } returns reviewEntity
        every { reviewPhotoWriter.write(any()) } returns reviewPhotoEntities

        val newReview = reviewService.write(writeReview)

        assertThat(newReview.type).isEqualTo(reviewEntity.type)
        assertThat(newReview.userId).isEqualTo(reviewEntity.userId)
        assertThat(newReview.orderItemId).isEqualTo(reviewEntity.orderItemId)
        assertThat(newReview.content).isEqualTo(reviewEntity.content)
        assertThat(newReview.score).isEqualTo(reviewEntity.score)
        newReview.photos.forEachIndexed { index, photo ->
            assertThat(photo.photoUrl).isEqualTo(reviewPhotoEntities[index].photoUrl)
        }
    }
}