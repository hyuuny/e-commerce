package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L

@Embeddable
data class DiscountPrice(
    @Column(nullable = false) val discountAmount: Long
) {
    init {
        require(discountAmount >= 0) { "할인 금액은 0보다 커야 합니다." }
    }

    constructor(discountAmount: BigDecimal) : this(discountAmount.toLong())

    companion object {
        val ZERO = DiscountPrice(ZERO_NUMBER)
    }
}