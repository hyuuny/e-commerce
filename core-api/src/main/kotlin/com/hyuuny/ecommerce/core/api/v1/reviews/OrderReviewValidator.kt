package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.core.api.v1.orders.OrderItemReader
import com.hyuuny.ecommerce.core.support.error.OrderItemNotFoundException
import com.hyuuny.ecommerce.core.support.error.ProductNotFoundException
import org.springframework.stereotype.Component

@Component
class OrderReviewValidator(
    private val orderItemReader: OrderItemReader,
    private val productReader: ProductReader,
) {
    fun validate(orderItemId: Long, productId: Long) {
        if (!orderItemReader.exists(orderItemId)) {
            throw OrderItemNotFoundException("주문 상품을 찾을 수 없습니다. id: $orderItemId")
        }

        if (!productReader.exists(productId)) {
            throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $productId")
        }
    }
}
