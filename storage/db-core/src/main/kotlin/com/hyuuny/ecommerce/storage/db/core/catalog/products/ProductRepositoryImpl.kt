package com.hyuuny.ecommerce.storage.db.core.catalog.products

import com.hyuuny.ecommerce.storage.db.core.catalog.categories.QCategoryProductEntity.categoryProductEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.QProductEntity.productEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class ProductRepositoryImpl : ProductRepositoryCustom, QuerydslRepositorySupport(ProductEntity::class.java) {
    override fun findAllBySearch(search: SearchProduct, pageable: Pageable): SimplePage<ProductEntity> {
        var query = from(productEntity)

        with(search) {
            categoryId?.let {
                query.join(categoryProductEntity).on(categoryProductEntity.productId.eq(productEntity.id))
                    .where(categoryProductEntity.categoryId.eq(it))
            }
            brandId?.let { query = query.where(productEntity.brandId.eq(it)) }
            name?.let { query = query.where(productEntity.name.containsIgnoreCase(it)) }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, productEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()

        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}