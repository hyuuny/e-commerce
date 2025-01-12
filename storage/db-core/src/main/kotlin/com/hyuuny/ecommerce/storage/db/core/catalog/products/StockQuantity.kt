package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val MINIMUM_QUANTITY_COUNT = 0L

@Embeddable
data class StockQuantity(
    @Column(nullable = false) val quantity: Long
) {
    init {
        require(quantity > MINIMUM_QUANTITY_COUNT) { "수량은 0보다 커야 합니다." }
    }

    operator fun minus(quantity: Long): StockQuantity {
        val newQuantity = this.quantity - quantity
        return StockQuantity(newQuantity)
    }
}