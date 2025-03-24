package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.constanct.RedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductViewService(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    fun increaseViewCount(productId: Long) {
        val key = "${RedisKey.PRODUCT_VIEW_COUNT_KEY.value}$productId"
        redisTemplate.opsForValue().increment(key)
    }
}
