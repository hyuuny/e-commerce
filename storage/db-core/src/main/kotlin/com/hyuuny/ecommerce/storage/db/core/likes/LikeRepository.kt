package com.hyuuny.ecommerce.storage.db.core.likes

import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<LikeEntity, Long> {
}
