package com.hyuuny.ecommerce.storage.db.core.catalog.categories

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "category_products",
    uniqueConstraints = [UniqueConstraint(columnNames = ["category_id", "product_id"])]
)
class CategoryProductEntity(
    @Column(nullable = false) val categoryId: Long,
    @Column(nullable = false) val productId: Long,
) : BaseEntity()
