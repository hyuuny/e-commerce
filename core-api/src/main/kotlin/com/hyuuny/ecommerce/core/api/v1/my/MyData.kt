package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.storage.db.core.catalog.products.DiscountPrice
import com.hyuuny.ecommerce.storage.db.core.catalog.products.Price
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductBadgeEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus
import com.hyuuny.ecommerce.storage.db.core.catalog.products.StockQuantity

data class MyLikedProductData(
    val productId: Long,
    val brandId: Long,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: StockQuantity,
    val isLiked: Boolean,
    val badges: List<MyLikedProductBadgeData>,
) {
    constructor(entity: ProductEntity, badges: List<ProductBadgeEntity>, isLiked: Boolean) : this(
        productId = entity.id,
        brandId = entity.brandId,
        status = entity.status,
        name = entity.name,
        thumbnailUrl = entity.thumbnailUrl,
        price = entity.price,
        discountPrice = entity.discountPrice,
        discountPercent = entity.calculateDiscountPercent().toInt(),
        totalPrice = entity.calculateTotalPrice(),
        stockQuantity = entity.stockQuantity,
        isLiked = isLiked,
        badges = badges.map { MyLikedProductBadgeData(it) },
    )
}

data class MyLikedProductBadgeData(
    val productId: Long,
    val title: String,
    val color: String,
    val bgColor: String,
) {
    constructor(productBadgeEntity: ProductBadgeEntity) : this(
        productId = productBadgeEntity.productId,
        title = productBadgeEntity.title,
        color = productBadgeEntity.color,
        bgColor = productBadgeEntity.bgColor,
    )
}
