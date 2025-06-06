package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class BrandService(
    private val brandReader: BrandReader,
) {
    @Cacheable(
        value = ["brandsSearch"],
        key = "#command.nameKo + '-' + #command.nameEn + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    fun search(command: BrandSearchCommand, pageable: Pageable): SimplePage<BrandData> {
        val page = brandReader.search(command, pageable)
        val brandData = page.content.map { BrandData(it) }
        return SimplePage(brandData, page)
    }

    @Cacheable(value = ["getBrand"], key = "#id")
    fun getBrand(id: Long): BrandView {
        val brandEntity = brandReader.read(id)
        return BrandView(brandEntity)
    }

    @CacheEvict(value = ["brandsSearch", "getBrand"], allEntries = true)
    fun cacheEvict() {
    }
}
