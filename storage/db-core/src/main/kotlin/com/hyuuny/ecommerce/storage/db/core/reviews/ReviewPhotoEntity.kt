package com.hyuuny.ecommerce.storage.db.core.reviews

import jakarta.persistence.*

@Table(
    name = "review_photos",
    indexes = [Index(name = "idx_review_id", columnList = "review_id")]
)
@Entity
class ReviewPhotoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val reviewId: Long,
    val photoUrl: String,
)
