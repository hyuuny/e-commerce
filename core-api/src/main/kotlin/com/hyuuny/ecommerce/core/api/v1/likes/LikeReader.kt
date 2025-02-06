package com.hyuuny.ecommerce.core.api.v1.likes

import com.hyuuny.ecommerce.storage.db.core.likes.LikeEntity
import com.hyuuny.ecommerce.storage.db.core.likes.LikeRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class LikeReader(
    private val repository: LikeRepository,
) {
    fun isLiked(userId: Long, productId: Long): LikeEntity? = repository.findByUserIdAndProductId(userId, productId)

    fun search(userId: Long, pageable: Pageable): SimplePage<LikeEntity> =
        repository.findAllByUserId(userId, pageable)
}
