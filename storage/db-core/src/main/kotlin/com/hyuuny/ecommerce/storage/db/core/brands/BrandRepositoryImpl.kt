package com.hyuuny.ecommerce.storage.db.core.brands

import com.hyuuny.ecommerce.storage.db.core.brands.QBrandEntity.brandEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import kotlin.math.min

class BrandRepositoryImpl : BrandRepositoryCustom, QuerydslRepositorySupport(BrandEntity::class.java) {

    override fun findAllBySearch(search: SearchBrand, pageable: Pageable): SimplePage<BrandEntity> {
        var query = from(brandEntity)

        with(search) {
            nameKo?.let { query = query.where(brandEntity.nameKo.like("%$nameKo%")) }
            nameEn?.let { query = query.where(brandEntity.nameEn.like("%$nameEn%")) }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, brandEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()
        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}
