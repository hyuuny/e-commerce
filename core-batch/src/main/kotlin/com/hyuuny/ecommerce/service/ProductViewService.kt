package com.hyuuny.ecommerce.service

import com.hyuuny.ecommerce.constanct.RedisKey
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductViewService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val productRepository: ProductRepository,
) {

    fun getViewCountMapAndRemove(): Map<Long, Int> {
        val redisKeys = redisTemplate.keys("${RedisKey.PRODUCT_VIEW_COUNT_KEY.value}*")
        return redisKeys.associate { key ->
            val productId = key.getProductId()
            val count = key.getCount()
            redisTemplate.delete(key)
            productId to count
        }
    }

    @Transactional
    fun updateViewCount(productId: Long, additionalCount: Int) {
        val product = productRepository.findByIdOrNull(productId)
        product?.increaseViewCount(additionalCount)
    }

    private fun String.getProductId(): Long {
        return this.removePrefix(RedisKey.PRODUCT_VIEW_COUNT_KEY.value).toLong()
    }

    private fun String.getCount(): Int {
        return (redisTemplate.opsForValue().get(this) as? Int) ?: 0
    }
}
