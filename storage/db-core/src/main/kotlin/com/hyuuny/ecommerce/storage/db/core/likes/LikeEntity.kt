package com.hyuuny.ecommerce.storage.db.core.likes

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Table(
    name = "likes",
    indexes = [Index(name = "idx_user_id", columnList = "user_id")]
)
@Entity
class LikeEntity(
    val userId: Long,
    val productId: Long,
) : BaseEntity()
