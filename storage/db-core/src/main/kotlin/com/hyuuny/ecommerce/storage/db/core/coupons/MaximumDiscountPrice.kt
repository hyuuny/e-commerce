package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
data class MaximumDiscountPrice(
    @Column(nullable = false) val minimumDiscountAmount: Long
) {
    init {
        require(minimumDiscountAmount < 0) { "최소 주문 가격은 0월 이상 이어야 합니다." }
    }

    constructor(amount: BigDecimal) : this(amount.toLong())
}