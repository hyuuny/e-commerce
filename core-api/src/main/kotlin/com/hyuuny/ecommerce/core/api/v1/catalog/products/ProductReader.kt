package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.support.error.ProductNotFoundException
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductRepository
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductReader(
    private val repository: ProductRepository,
) {
    fun search(command: ProductSearchCommand, pageable: Pageable): SimplePage<ProductEntity> =
        repository.findAllBySearch(command.toSearch(), pageable)

    fun read(id: Long): ProductEntity = repository.findByIdOrNull(id)
        ?: throw ProductNotFoundException("상품을 찾을 수 없습니다. id: $id")

    fun readAllByIds(ids: List<Long>): List<ProductEntity> = repository.findAllById(ids)

    fun exists(id: Long): Boolean = repository.existsById(id)
}
