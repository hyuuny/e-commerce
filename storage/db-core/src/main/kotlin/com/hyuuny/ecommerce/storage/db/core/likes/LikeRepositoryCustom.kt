package com.hyuuny.ecommerce.storage.db.core.likes

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface LikeRepositoryCustom {
    fun findAllByUserId(userId: Long, pageable: Pageable): SimplePage<LikeEntity>
}
