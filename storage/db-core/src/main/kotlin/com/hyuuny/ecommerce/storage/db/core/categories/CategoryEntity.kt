package com.hyuuny.ecommerce.storage.db.core.categories

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*

@Table(name = "categories")
@Entity
class CategoryEntity(
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id")
    var parent: CategoryEntity? = null,
    val name: String,
    val iconImageUrl: String? = null,
    val hide: Boolean = false,
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    val children: MutableList<CategoryEntity> = mutableListOf()
) : BaseEntity() {

    fun addChild(child: CategoryEntity) {
        this.children.add(child)
        child.parent = this
    }

    fun getParentId(): Long = parent!!.id

    fun getIconImageUrlOrDefaultImageUrl(): String = iconImageUrl ?: "categories/icons/default-image.png"
}
