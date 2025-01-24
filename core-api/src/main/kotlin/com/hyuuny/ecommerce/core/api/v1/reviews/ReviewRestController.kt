package com.hyuuny.ecommerce.core.api.v1.reviews

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
