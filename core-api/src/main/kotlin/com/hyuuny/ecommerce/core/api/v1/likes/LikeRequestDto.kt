package com.hyuuny.ecommerce.core.api.v1.likes

data class LikeRequestDto(
    val productId: Long,
) {
    fun toCommand(userId: Long) = LikeCommand(
        userId = userId,
        productId = productId,
    )
}
