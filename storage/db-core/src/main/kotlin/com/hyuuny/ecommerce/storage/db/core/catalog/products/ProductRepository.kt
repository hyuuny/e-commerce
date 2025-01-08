package com.hyuuny.ecommerce.storage.db.core.catalog.products

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {
}