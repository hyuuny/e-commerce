package com.hyuuny.ecommerce.storage.db.core.catalog.products

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Table(name = "products")
@Entity
class ProductEntity(
    val brandId: Long,
    status: ProductStatus = ProductStatus.ON_SALE,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    stockQuantity: StockQuantity,
    viewCount: ViewCount = ViewCount.init(),
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    var stockQuantity = stockQuantity
        protected set

    var viewCount = viewCount
        protected set

    fun calculateDiscountPercent(): Double {
        return (discountPrice.discountAmount.toDouble() / price.amount.toDouble()) * 100
    }

    fun calculateTotalPrice(): Long {
        return (price.amount - discountPrice.discountAmount).coerceAtLeast(0)
    }

    fun isInsufficientStock(quantity: Long): Boolean {
        return stockQuantity.quantity < quantity
    }

    fun decrease(quantity: Long) {
        stockQuantity -= quantity
        if (stockQuantity.quantity <= 0) soldOut()
    }

    fun soldOut() {
        status = ProductStatus.SOLD_OUT
    }

    fun increaseViewCount(additionalCount: Int) {
        viewCount += additionalCount
    }
}