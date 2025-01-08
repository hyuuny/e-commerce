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
    @Enumerated(EnumType.STRING) val status: ProductStatus = ProductStatus.ON_SALE,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    val stockQuantity: StockQuantity,
) : BaseEntity() {

    fun calculateDiscountPercent(): Double {
        return (discountPrice.discountAmount.toDouble() / price.amount.toDouble()) * 100
    }

    fun calculateTotalPrice(): Long {
        return (price.amount - discountPrice.discountAmount).coerceAtLeast(0)
    }
}