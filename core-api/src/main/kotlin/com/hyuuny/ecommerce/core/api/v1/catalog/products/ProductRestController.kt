package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/products")
@RestController
class ProductRestController(
    private val service: ProductService,
    private val productViewService: ProductViewService,
) {
    @GetMapping
    fun search(
        request: ProductSearchRequestDto,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ApiResponse<SimplePage<ProductResponseDto>> {
        val page = service.search(request.toCommand(), pageable)
        return ApiResponse.success(SimplePage(page.content.map { ProductResponseDto(it) }, page))
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ApiResponse<ProductViewResponseDto> {
        val product = service.getProduct(id)
        productViewService.increaseViewCount(id)
        return ApiResponse.success(ProductViewResponseDto(product))
    }
}
