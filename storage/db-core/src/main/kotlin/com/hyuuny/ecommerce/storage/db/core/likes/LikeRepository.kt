package com.hyuuny.ecommerce.storage.db.core.likes

import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<LikeEntity, Long> {
    fun findByUserIdAndProductId(userId: Long, productId: Long): LikeEntity?
    fun deleteByUserIdAndProductId(userId: Long, productId: Long)
}
