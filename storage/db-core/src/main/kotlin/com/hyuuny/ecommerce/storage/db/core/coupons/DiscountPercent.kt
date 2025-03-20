package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_RATE = 0.0

@Embeddable
data class DiscountPercent(
    val discountRate: Double?
) {
    init {
        discountRate?.let { require(it >= ZERO_RATE) { "할인율은 0% 이상이어야 합니다." } }
    }

    constructor(rate: BigDecimal) : this(rate.toDouble())

    companion object {
        val ZERO = DiscountPercent(ZERO_RATE)
    }
}
