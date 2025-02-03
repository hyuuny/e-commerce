package com.hyuuny.ecommerce.core.api.v1.likes

import com.hyuuny.ecommerce.storage.db.core.likes.LikeEntity

data class LikeCommand(
    val userId: Long,
    val productId: Long,
) {
    fun toEntity(): LikeEntity = LikeEntity(
        userId = userId,
        productId = productId,
    )
}
