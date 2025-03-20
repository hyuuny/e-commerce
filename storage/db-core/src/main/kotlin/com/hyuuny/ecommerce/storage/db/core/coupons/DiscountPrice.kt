package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L

@Embeddable
data class DiscountPrice(
    val discountAmount: Long
) {
    init {
        discountAmount?.let { require(it >= ZERO_NUMBER) { "할인 금액은 0원 이상이어야 합니다." } }

    }

    constructor(amount: BigDecimal) : this(amount.toLong())

    companion object {
        val ZERO = DiscountPrice(ZERO_NUMBER)
    }
}