package com.hyuuny.ecommerce.storage.db.core.catalog.products

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface ProductRepositoryCustom {
    fun findAllBySearch(search: SearchProduct, pageable: Pageable): SimplePage<ProductEntity>
}