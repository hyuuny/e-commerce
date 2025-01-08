package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.math.BigDecimal

private const val ZERO_NUMBER = 0L

@Embeddable
data class Price(
    @Column(nullable = false) val amount: Long
) {
    init {
        require(amount >= 0) { "금액은 0보다 커야 합니다." }
    }

    constructor(amount: BigDecimal) : this(amount.toLong())

    companion object {
        val ZERO = Price(ZERO_NUMBER)
    }
}