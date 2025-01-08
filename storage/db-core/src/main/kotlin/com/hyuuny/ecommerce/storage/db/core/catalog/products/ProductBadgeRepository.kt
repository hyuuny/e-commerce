package com.hyuuny.ecommerce.storage.db.core.catalog.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductBadgeRepository : JpaRepository<ProductBadgeEntity, Long> {
    fun findAllByProductId(id: Long): List<ProductBadgeEntity>
    fun findAllByProductIdIn(ids: List<Long>): List<ProductBadgeEntity>
}