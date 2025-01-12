package com.hyuuny.ecommerce.storage.db.core.orders

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val ZERO_NUMBER = 0L

@Embeddable
data class TotalDiscountPrice(
    @Column(nullable = false) val totalDiscountAmount: Long
) {
    init {
        require(totalDiscountAmount >= ZERO_NUMBER) { "금액은 0보다 커야 합니다." }
    }
}
