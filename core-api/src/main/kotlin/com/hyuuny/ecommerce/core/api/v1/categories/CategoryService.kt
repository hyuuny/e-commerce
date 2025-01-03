package com.hyuuny.ecommerce.core.api.v1.categories

import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryReader: CategoryReader,
) {
    fun getAllParentCategory(): List<ParentCategoryData> =
        categoryReader.readAllParentCategory().filterNot { it.hide }
            .map { ParentCategoryData(it) }

    fun getAllChildrenCategory(parentId: Long): List<ChildCategoryData> =
        categoryReader.readAllChildrenCategory(parentId).filterNot { it.hide }
            .map { ChildCategoryData(it) }
}
