package com.hyuuny.ecommerce.storage.db.core.catalog.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductBannerRepository : JpaRepository<ProductBannerEntity, Long> {
    fun findAllByProductId(id: Long): List<ProductBannerEntity>
}