package com.hyuuny.ecommerce.core.api.v1.categories

import ChildCategoryResponseDto
import ParentCategoryResponseDto
import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/categories")
@RestController
class CategoryRestController(
    private val service: CategoryService
) {
    @GetMapping("/parents")
    fun getAllParentCategory(): ApiResponse<List<ParentCategoryResponseDto>> {
        val parentCategories = service.getAllParentCategory()
        return ApiResponse.success(parentCategories.map { ParentCategoryResponseDto(it) })
    }

    @GetMapping("/parents/{id}/children")
    fun getAllChildrenCategory(@PathVariable("id") id: Long): ApiResponse<List<ChildCategoryResponseDto>> {
        val childrenCategory = service.getAllChildrenCategory(id)
        return ApiResponse.success(childrenCategory.map { ChildCategoryResponseDto(it) })
    }
}
