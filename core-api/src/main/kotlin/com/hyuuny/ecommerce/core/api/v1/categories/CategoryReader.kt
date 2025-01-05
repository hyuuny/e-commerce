package com.hyuuny.ecommerce.core.api.v1.categories

import com.hyuuny.ecommerce.core.support.error.CategoryNotFoundException
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryEntity
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CategoryReader(
    private val repository: CategoryRepository,
) {
    fun read(id: Long): CategoryEntity = repository.findByIdOrNull(id)
        ?: throw CategoryNotFoundException("카테고리를 찾을 수 없습니다. id: $id")

    fun readAllParentCategory(): List<CategoryEntity> = repository.findAllByParentIsNull()

    fun readAllChildrenCategory(parentId: Long): List<CategoryEntity> = repository.findAllByParentId(parentId)
}
