package com.hyuuny.ecommerce.storage.db.core.categories

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByParentIsNull(): List<CategoryEntity>
    fun findAllByParentId(id: Long): List<CategoryEntity>
}
