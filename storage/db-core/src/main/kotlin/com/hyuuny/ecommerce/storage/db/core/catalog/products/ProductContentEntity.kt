package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.*

@Table(
    name = "product_contents",
    indexes = [Index(name = "idx_product_id", columnList = "product_id")]
)
@Entity
class ProductContentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val productId: Long,
    val imageUrl: String,
)