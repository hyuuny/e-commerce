package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.error.OrderItemNotFoundException
import com.hyuuny.ecommerce.core.support.error.ProductNotFoundException
import com.hyuuny.ecommerce.core.support.error.ReviewNotFoundException
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
import org.junit.jupiter.api.assertThrows

class ReviewServiceTest {
    private lateinit var reviewWriter: ReviewWriter
    private lateinit var reviewReader: ReviewReader
    private lateinit var reviewPhotoWriter: ReviewPhotoWriter
    private lateinit var reviewPhotoReader: ReviewPhotoReader
    private lateinit var validator: OrderReviewValidator
    private lateinit var reviewService: ReviewService

    @BeforeEach
    fun setUp() {
        reviewWriter = mockk()
        reviewReader = mockk()
        reviewPhotoWriter = mockk()
        reviewPhotoReader = mockk()
        validator = mockk()
        reviewService = ReviewService(reviewWriter, reviewReader, reviewPhotoWriter, reviewPhotoReader, validator)
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

    @Test
    fun `주문 상품이 존재하지 않으면 후기를 작성할 수 없다`() {
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
        every { validator.validate(any(), any()) } throws OrderItemNotFoundException("주문 상품을 찾을 수 없습니다. id: 1")

        val exception = assertThrows<OrderItemNotFoundException> {
            reviewService.write(writeReview)
        }
        assertThat(exception.message).isEqualTo(ErrorType.ORDER_ITEM_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("주문 상품을 찾을 수 없습니다. id: 1")
    }

    @Test
    fun `주문 상품의 상품이 존재하지 않으면 후기를 작성할 수 없다`() {
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
        every { validator.validate(any(), any()) } throws ProductNotFoundException("상품을 찾을 수 없습니다. id: 1")

        val exception = assertThrows<ProductNotFoundException> {
            reviewService.write(writeReview)
        }
        assertThat(exception.message).isEqualTo(ErrorType.PRODUCT_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("상품을 찾을 수 없습니다. id: 1")
    }

    @Test
    fun `후기를 상세 조회 할 수 있다`() {
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
        every { reviewReader.read(any()) } returns reviewEntity
        every { reviewPhotoReader.readAll(any()) } returns reviewPhotoEntities

        val reviewView = reviewService.getReview(reviewEntity.id)

        assertThat(reviewView.type).isEqualTo(reviewEntity.type)
        assertThat(reviewView.userId).isEqualTo(reviewEntity.userId)
        assertThat(reviewView.orderItemId).isEqualTo(reviewEntity.orderItemId)
        assertThat(reviewView.productId).isEqualTo(reviewEntity.productId)
        assertThat(reviewView.content).isEqualTo(reviewEntity.content)
        assertThat(reviewView.score).isEqualTo(reviewEntity.score)
        reviewView.photos.forEachIndexed { index, photo ->
            assertThat(photo.photoUrl).isEqualTo(reviewPhotoEntities[index].photoUrl)
        }
    }

    @Test
    fun `존재하지 않는 후기를 상세 조회 할 수 없다`() {
        val invalidId = 9L
        every { reviewReader.read(any()) } throws ReviewNotFoundException("후기를 찾을 수 없습니다. id: $invalidId")

        val exception = assertThrows<ReviewNotFoundException> {
            reviewService.getReview(invalidId)
        }

        assertThat(exception.message).isEqualTo(ErrorType.REVIEW_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("후기를 찾을 수 없습니다. id: $invalidId")
    }
}