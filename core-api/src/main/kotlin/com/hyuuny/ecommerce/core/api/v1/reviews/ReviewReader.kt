package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.support.error.ReviewNotFoundException
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewEntity
import com.hyuuny.ecommerce.storage.db.core.reviews.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ReviewReader(
    private val repository: ReviewRepository,
) {
    fun read(id: Long): ReviewEntity = repository.findByIdOrNull(id)
        ?: throw ReviewNotFoundException("후기를 찾을 수 없습니다. id: $id")
}
