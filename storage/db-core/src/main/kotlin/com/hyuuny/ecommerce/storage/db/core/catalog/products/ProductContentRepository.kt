package com.hyuuny.ecommerce.storage.db.core.catalog.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductContentRepository : JpaRepository<ProductContentEntity, Long> {
    fun findAllByProductId(id: Long): List<ProductContentEntity>
}