package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/reviews")
@RestController
class ReviewRestController(
    private val service: ReviewService,
) {
    @PostMapping
    fun write(@RequestBody request: WriteReviewRequestDto): ApiResponse<ReviewViewResponseDto> {
        val reviewView = service.write(request.toWriteReview())
        return ApiResponse.success(ReviewViewResponseDto(reviewView))
    }

    @GetMapping("/{id}")
    fun getReview(@PathVariable id: Long): ApiResponse<ReviewViewResponseDto> {
        val review = service.getReview(id)
        return ApiResponse.success(ReviewViewResponseDto(review))
    }

    @GetMapping
    fun search(
        request: ReviewSearchRequestDto,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResponse<SimplePage<ReviewResponseDto>> {
        val page = service.search(request.toCommand(), pageable)
        return ApiResponse.success(SimplePage(page.content.map { ReviewResponseDto(it) }, page))
    }
}
