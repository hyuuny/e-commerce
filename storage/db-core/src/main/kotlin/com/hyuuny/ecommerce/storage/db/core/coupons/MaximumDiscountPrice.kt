package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class MaximumDiscountPrice(
    val maximumDiscountAmount: Long
) {
    init {
        require(maximumDiscountAmount >= 0) { "최대 할인 금액은 0원 이상 이어야 합니다." }
    }

    constructor(amount: BigDecimal) : this(amount.toLong())
}