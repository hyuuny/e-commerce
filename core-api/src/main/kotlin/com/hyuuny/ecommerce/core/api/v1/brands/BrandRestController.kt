package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/brands")
@RestController
class BrandRestController(
    private val service: BrandService,
) {
    @GetMapping
    fun search(
        request: BrandSearchRequestDto,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResponse<SimplePage<BrandResponseDto>> {
        val page = service.search(request.toCommand(), pageable)
        return ApiResponse.success(SimplePage(page.content.map { BrandResponseDto(it) }, page))
    }

    @GetMapping("/{id}")
    fun getBrand(@PathVariable id: Long): ApiResponse<BrandViewResponseDto> {
        val brandView = service.getBrand(id)
        return ApiResponse.success(BrandViewResponseDto(brandView))
    }
}
