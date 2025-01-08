package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.api.v1.brands.BrandReader
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class ProductService(
    private val productReader: ProductReader,
    private val productBannerReader: ProductBannerReader,
    private val productContentReader: ProductContentReader,
    private val productBadgeReader: ProductBadgeReader,
    private val brandReader: BrandReader,
) {
    @Cacheable(
        value = ["productsSearch"],
        key = "#command.categoryId + '-' + #command.brandId + '-' + #command.name + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    fun search(command: ProductSearchCommand, pageable: Pageable): SimplePage<ProductData> {
        val page = productReader.search(command, pageable)
        val badgeGroup = productBadgeReader.readAllByIds(page.content.map { it.id }).groupBy { it.productId }
        return SimplePage(page.content.mapNotNull {
            val badges = badgeGroup[it.id] ?: return@mapNotNull null
            ProductData(it, badges)
        }, page)
    }

    @CacheEvict(value = ["productsSearch", "getProduct"], allEntries = true)
    fun cacheEvict() {
    }
}
