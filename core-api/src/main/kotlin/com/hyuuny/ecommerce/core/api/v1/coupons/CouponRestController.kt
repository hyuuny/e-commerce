package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.support.AuthUserId
import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/coupons")
@RestController
class CouponRestController(
    private val service: CouponService,
) {
    @PostMapping
    fun issueUserCoupon(@RequestBody request: IssueUserCouponReqeustDto): ApiResponse<UserCouponResponseDto> {
        val userCoupon = service.issueCouponToUser(request.toCommand())
        return ApiResponse.success(UserCouponResponseDto(userCoupon))
    }

    @GetMapping("/{id}")
    fun getUserCoupon(
        @AuthUserId userId: Long,
        @PathVariable id: Long,
    ): ApiResponse<UserCouponViewResponseDto> {
        val userCoupon = service.getUserCoupon(userId, id)
        return ApiResponse.success(UserCouponViewResponseDto(userCoupon))
    }
}