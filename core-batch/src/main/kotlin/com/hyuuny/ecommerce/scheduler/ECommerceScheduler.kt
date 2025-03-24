package com.hyuuny.ecommerce.scheduler

import com.hyuuny.ecommerce.logging.Log
import com.hyuuny.ecommerce.service.ProductViewService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ECommerceScheduler(
    private val productViewService: ProductViewService,
) {
    companion object : Log

    @Scheduled(cron = "0 0 * * * *")
    fun updateProductViewCount() {
        val viewCountMap = productViewService.getViewCountMapAndRemove()

        if (viewCountMap.isNotEmpty()) {
            viewCountMap.forEach { (productId, viewCount) ->
                log.info("productId:${productId}, viewCount:${viewCount}")
                productViewService.updateViewCount(productId, viewCount)
            }
        }
    }
}
