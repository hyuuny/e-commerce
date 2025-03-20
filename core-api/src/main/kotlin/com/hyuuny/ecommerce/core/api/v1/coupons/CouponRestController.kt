package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/coupons")
@RestController
class CouponRestController(
    private val service: CouponService,
) {
    @PostMapping
    fun issueUserCoupon(@RequestBody request: IssueUserCouponReqeustDto): ApiResponse<UserCouponResponseDto> {
        val userCouponEntity = service.issueCouponToUser(request.toCommand())
        return ApiResponse.success(UserCouponResponseDto(userCouponEntity))
    }
}