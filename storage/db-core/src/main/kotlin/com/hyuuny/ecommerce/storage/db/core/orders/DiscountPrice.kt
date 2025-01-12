package com.hyuuny.ecommerce.storage.db.core.orders

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val ZERO_NUMBER = 0L

@Embeddable
data class DiscountPrice(
    @Column(nullable = false) val discountAmount: Long
) {
    init {
        require(discountAmount >= ZERO_NUMBER) { "할인 금액은 0보다 커야 합니다." }
    }
}
