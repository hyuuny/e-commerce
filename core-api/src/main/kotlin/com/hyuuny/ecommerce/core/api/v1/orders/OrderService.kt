package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.core.support.error.CheckoutTimeoutException
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Transactional(readOnly = true)
@Service
class OrderService(
    private val orderWriter: OrderWriter,
    private val orderItemWriter: OrderItemWriter,
    private val productReader: ProductReader,
    private val redissonClient: RedissonClient
) {
    companion object {
        private const val PRODUCT_LOCK = "productLock"
        private const val PRODUCT_TRY_LOCK_TIME = 10L
    }

    @Transactional
    fun checkout(command: Checkout): OrderView {
        val productIds = command.items.map { it.productId }
        val locks = productIds.map { redissonClient.getLock("$PRODUCT_LOCK:$it") }
        locks.forEach {
            val acquired = it.tryLock(PRODUCT_TRY_LOCK_TIME, TimeUnit.SECONDS)
            if (!acquired) throw CheckoutTimeoutException("제품에 대한 잠금을 획득할 수 없습니다. name: ${it.name}")
        }

        try {
            val newOrder = orderWriter.checkout(command)
            val productMap = productReader.readAllByIds(productIds).associateBy { it.id }
            val newOrderItems = orderItemWriter.append(command.items, newOrder, productMap)
            return OrderView(newOrder, newOrderItems)
        } finally {
            locks.forEach { it.unlock() }
        }
    }
}
