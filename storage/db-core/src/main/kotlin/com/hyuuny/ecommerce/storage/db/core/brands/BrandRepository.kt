package com.hyuuny.ecommerce.storage.db.core.brands

import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<BrandEntity, Long>, BrandRepositoryCustom {
}
