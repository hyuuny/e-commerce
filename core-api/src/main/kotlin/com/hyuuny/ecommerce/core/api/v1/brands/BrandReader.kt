package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.core.support.error.BrandNotFoundException
import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity
import com.hyuuny.ecommerce.storage.db.core.brands.BrandRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BrandReader(
    private val repository: BrandRepository,
) {
    fun read(id: Long): BrandEntity = repository.findByIdOrNull(id)
        ?: throw BrandNotFoundException("브랜드를 찾을 수 없습니다. id: $id")

    fun findAllBySearch(searchCommand: BrandSearchCommand, pageable: Pageable): SimplePage<BrandEntity> =
        repository.findAllBySearch(searchCommand.toSearch(), pageable)
}
