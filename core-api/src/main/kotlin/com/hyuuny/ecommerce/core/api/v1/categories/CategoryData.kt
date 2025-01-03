package com.hyuuny.ecommerce.core.api.v1.categories

import com.hyuuny.ecommerce.storage.db.core.categories.CategoryEntity

data class ParentCategoryData(
    val id: Long,
    val name: String,
    val iconImageUrl: String,
    val hide: Boolean
){
    constructor(category: CategoryEntity) : this (
        id = category.id,
        name = category.name,
        iconImageUrl = category.getIconImageUrlOrDefaultImageUrl(),
        hide = category.hide
    )
}

data class ChildCategoryData(
    val id: Long,
    val parentId: Long,
    val name: String,
    val hide: Boolean,
) {
    constructor(childCategory: CategoryEntity) : this(
        id = childCategory.id,
        parentId = childCategory.getParentId(),
        name = childCategory.name,
        hide = childCategory.hide,
    )
}
