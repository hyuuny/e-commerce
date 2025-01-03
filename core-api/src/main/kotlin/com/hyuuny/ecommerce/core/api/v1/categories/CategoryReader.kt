package com.hyuuny.ecommerce.core.api.v1.categories

import com.hyuuny.ecommerce.storage.db.core.categories.CategoryEntity
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryRepository
import org.springframework.stereotype.Component

@Component
class CategoryReader(
    private val repository: CategoryRepository,
) {
    fun readAllParentCategory(): List<CategoryEntity> = repository.findAllByParentIsNull()

    fun readAllChildrenCategory(parentId: Long): List<CategoryEntity> = repository.findAllByParentId(parentId)
}
