package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.api.v1.users.UserReader
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ReviewService(
    private val reviewWriter: ReviewWriter,
    private val reviewReader: ReviewReader,
    private val reviewPhotoWriter: ReviewPhotoWriter,
    private val reviewPhotoReader: ReviewPhotoReader,
    private val userReader: UserReader,
    private val validator: OrderReviewValidator,
) {
    @CacheEvict(value = ["reviewSearch", "getReview"], allEntries = true)
    @Transactional
    fun write(writeReview: WriteReview): ReviewViewData {
        validator.validate(writeReview.orderItemId, writeReview.productId)
        val newReview = reviewWriter.write(writeReview.toEntity())
        val newReviewPhotos = reviewPhotoWriter.write(writeReview.photos.map { it.toEntity(newReview.id) })
        return ReviewViewData(newReview, newReviewPhotos)
    }

    @Cacheable(value = ["getReview"], key = "#id")
    fun getReview(id: Long): ReviewViewData {
        val review = reviewReader.read(id)
        val reviewPhotos = reviewPhotoReader.readAll(review.id)
        return ReviewViewData(review, reviewPhotos)
    }

    @Cacheable(
        value = ["reviewSearch"],
        key = "#command.productId + '-' + #command.userId + '-' + #command.type + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    fun search(command: ReviewSearchCommand, pageable: Pageable): SimplePage<ReviewData> {
        val page = reviewReader.search(command, pageable)
        val photoGroup = reviewPhotoReader.readAllByReviewIds(page.content.map { it.id }).groupBy { it.reviewId }
        val userMap = userReader.readAllByIds(page.content.map { it.userId }).associateBy { it.id }
        return SimplePage(page.content.mapNotNull { review ->
            val user = userMap[review.userId] ?: return@mapNotNull null
            val photos = photoGroup[review.id] ?: return@mapNotNull null
            ReviewData(review, user, photos)
        }, page)
    }
}
