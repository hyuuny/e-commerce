package com.hyuuny.ecommerce.core.api.v1.categories

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryReader: CategoryReader,
) {
    @Cacheable(value = ["parentCategories"], key = "#root.method.name")
    fun getAllParentCategory(): List<ParentCategoryData> =
        categoryReader.readAllParentCategory().filterNot { it.hide }
            .map { ParentCategoryData(it) }

    @Cacheable(value = ["childrenCategories"], key = "#parentId", unless = "#result.isEmpty()")
    fun getAllChildrenCategory(parentId: Long): List<ChildCategoryData> {
        val parentCategory = categoryReader.read(parentId)
        return categoryReader.readAllChildrenCategory(parentCategory.id).filterNot { it.hide }
            .map { ChildCategoryData(it) }
    }

    @CacheEvict(value = ["parentCategories", "childrenCategories"], allEntries = true)
    fun cacheEvict() {
    }
}
