package com.hyuuny.ecommerce.storage.db.core.reviews

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Table(
    name = "reviews",
    indexes = [Index(name = "idx_product_id", columnList = "product_id")],
)
@Entity
class ReviewEntity(
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    @Lob val content: String,
    val score: Score,
) : BaseEntity()
