package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*

data class ProductData(
    val id: Long,
    val brandId: Long,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: StockQuantity,
    val badges: List<ProductBadgeData>,
) {
    constructor(entity: ProductEntity, badges: List<ProductBadgeEntity>) : this(
        id = entity.id,
        brandId = entity.brandId,
        status = entity.status,
        name = entity.name,
        thumbnailUrl = entity.thumbnailUrl,
        price = entity.price,
        discountPrice = entity.discountPrice,
        discountPercent = entity.calculateDiscountPercent().toInt(),
        totalPrice = entity.calculateTotalPrice(),
        stockQuantity = entity.stockQuantity,
        badges = badges.map { ProductBadgeData(it) },
    )
}

data class ProductBannerData(
    val productId: Long,
    val imageUrl: String,
) {
    constructor(productBannerEntity: ProductBannerEntity) : this(
        productId = productBannerEntity.productId,
        imageUrl = productBannerEntity.imageUrl,
    )
}

data class ProductContentData(
    val productId: Long,
    val imageUrl: String,
) {
    constructor(productContentEntity: ProductContentEntity) : this(
        productId = productContentEntity.productId,
        imageUrl = productContentEntity.imageUrl,
    )
}

data class ProductBadgeData(
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

data class ProductView(
    val id: Long,
    val brandId: Long,
    val brandName: String,
    val status: ProductStatus,
    val name: String,
    val thumbnailUrl: String,
    val price: Price,
    val discountPrice: DiscountPrice,
    val discountPercent: Int,
    val totalPrice: Long,
    val stockQuantity: StockQuantity,
    val banners: List<ProductBannerData>,
    val contents: List<ProductContentData>,
    val badges: List<ProductBadgeData>,
) {
    constructor(
        brand: BrandEntity,
        entity: ProductEntity,
        banners: List<ProductBannerEntity>,
        contents: List<ProductContentEntity>,
        badges: List<ProductBadgeEntity>,
    ) : this(
        id = entity.id,
        brandId = brand.id,
        brandName = brand.nameKo,
        status = entity.status,
        name = entity.name,
        thumbnailUrl = entity.thumbnailUrl,
        price = Price(entity.price.amount),
        discountPrice = DiscountPrice(entity.discountPrice.discountAmount),
        discountPercent = entity.calculateDiscountPercent().toInt(),
        totalPrice = entity.calculateTotalPrice(),
        stockQuantity = StockQuantity(entity.stockQuantity.quantity),
        banners = banners.map { ProductBannerData(it) },
        contents = contents.map { ProductContentData(it) },
        badges = badges.map { ProductBadgeData(it) },
    )
}