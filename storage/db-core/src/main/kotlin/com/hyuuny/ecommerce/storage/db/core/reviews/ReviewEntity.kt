package com.hyuuny.ecommerce.storage.db.core.reviews

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*

@Table(
    name = "reviews",
    indexes = [Index(name = "idx_product_id", columnList = "product_id")],
)
@Entity
class ReviewEntity(
    type: ReviewType = ReviewType.TEXT,
    val userId: Long,
    val orderItemId: Long,
    val productId: Long,
    @Lob val content: String,
    val score: Score,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var type: ReviewType = type
        protected set
}