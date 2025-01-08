package com.hyuuny.ecommerce.storage.db.core.catalog.categories

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryProductRepository : JpaRepository<CategoryProductEntity, Long> {
}