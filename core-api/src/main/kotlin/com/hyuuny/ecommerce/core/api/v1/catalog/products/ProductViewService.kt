package com.hyuuny.ecommerce.core.api.v1.catalog.products

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductViewService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val productReader: ProductReader,
) {
    companion object {
        private const val PRODUCT_VIEW_COUNT_KEY = "productViewCount:"
    }

    fun increaseViewCount(productId: Long) {
        val key = "$PRODUCT_VIEW_COUNT_KEY$productId"
        redisTemplate.opsForValue().increment(key)
    }

    fun getViewCountMapAndRemove(): Map<Long, Int> {
        val redisKeys = redisTemplate.keys("$PRODUCT_VIEW_COUNT_KEY*")
        return redisKeys.associate { key ->
            val productId = key.getProductId()
            val count = key.getCount()
            redisTemplate.delete(key)
            productId to count
        }
    }

    @Transactional
    fun updateViewCount(productId: Long, additionalCount: Int) {
        val product = productReader.read(productId)
        product.increaseViewCount(additionalCount)
    }

    private fun String.getProductId(): Long {
        return this.removePrefix(PRODUCT_VIEW_COUNT_KEY).toLong()
    }

    private fun String.getCount(): Int {
        return (redisTemplate.opsForValue().get(this) as? Int) ?: 0
    }
}
