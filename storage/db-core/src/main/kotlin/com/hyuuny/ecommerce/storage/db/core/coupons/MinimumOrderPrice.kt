package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L
private const val INVALID_AMOUNT = -1

@Embeddable
data class MinimumOrderPrice(
    @Column(nullable = false) val minimumOrderAmount: Long
) {
    init {
        require(minimumOrderAmount > INVALID_AMOUNT) { "최소 주문 가격은 0원 이상 이어야 합니다." }
    }

    constructor(amount: BigDecimal) : this(amount.toLong())

    companion object {
        val ZERO = MinimumOrderPrice(ZERO_NUMBER)
    }
}