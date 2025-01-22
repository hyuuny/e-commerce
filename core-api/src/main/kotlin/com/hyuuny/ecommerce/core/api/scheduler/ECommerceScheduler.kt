package com.hyuuny.ecommerce.core.api.scheduler

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductViewService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ECommerceScheduler(
    private val productViewService: ProductViewService,
) {
    @Scheduled(cron = "0 0 * * * *")
    fun updateProductViewCount() {
        val viewCountMap = productViewService.getViewCountMapAndRemove()

        if (viewCountMap.isNotEmpty()) {
            viewCountMap.forEach { (productId, viewCount) ->
                productViewService.updateViewCount(productId, viewCount)
            }
        }
    }
}
