package com.hyuuny.ecommerce.core.api.v1.likes

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val likeReader: LikeReader,
    private val likeWriter: LikeWriter,
) {
    @Transactional
    fun toggleLike(command: LikeCommand) {
        likeReader.isLiked(command.userId, command.productId)?.let { like ->
            likeWriter.unlike(like)
        } ?: likeWriter.like(command)
    }
}
