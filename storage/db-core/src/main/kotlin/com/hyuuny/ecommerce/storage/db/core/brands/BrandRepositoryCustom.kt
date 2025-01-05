package com.hyuuny.ecommerce.storage.db.core.brands

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface BrandRepositoryCustom {
    fun findAllBySearch(search: SearchBrand, pageable: Pageable): SimplePage<BrandEntity>
}
