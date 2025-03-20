package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.core.support.AuthUserId
import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/my")
@RestController
class MyRestController(
    private val service: MyService,
) {
    @GetMapping("/liked-products")
    fun getAllLikedProducts(
        @AuthUserId userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResponse<SimplePage<MyLikedProductResponseDto>> {
        val page = service.getAllLikedProducts(userId, pageable)
        return ApiResponse.success(SimplePage(page.content.map { MyLikedProductResponseDto(it) }, page))
    }

    @GetMapping("/coupons")
    fun getUserCoupons(
        reqeustDto: UserCouponSearchReqeustDto,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ApiResponse<SimplePage<UserCouponResponseDto>> {
        val page = service.getAllUserCoupons(reqeustDto.toCommand(), pageable)
        return ApiResponse.success(SimplePage(page.content.map { UserCouponResponseDto(it) }, page))
    }
}
