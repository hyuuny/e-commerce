package com.hyuuny.ecommerce.core.api.v1.likes

import com.hyuuny.ecommerce.core.support.AuthUserId
import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/likes")
@RestController
class LikeRestController(
    private val service: LikeService,
) {
    @PostMapping
    fun toggleLike(
        @AuthUserId userId: Long,
        @RequestBody request: LikeRequestDto,
    ): ApiResponse<Any> {
        service.toggleLike(request.toCommand(userId))
        return ApiResponse.success()
    }
}
