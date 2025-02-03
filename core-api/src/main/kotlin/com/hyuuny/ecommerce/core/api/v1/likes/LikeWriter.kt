package com.hyuuny.ecommerce.core.api.v1.likes

import com.hyuuny.ecommerce.storage.db.core.likes.LikeEntity
import com.hyuuny.ecommerce.storage.db.core.likes.LikeRepository
import org.springframework.stereotype.Component

@Component
class LikeWriter(
    private val repository: LikeRepository,
) {
    fun like(command: LikeCommand): LikeEntity {
        val likeEntity = command.toEntity()
        return repository.save(likeEntity)
    }

    fun unlike(like: LikeEntity) = repository.deleteByUserIdAndProductId(like.userId, like.productId)
}
