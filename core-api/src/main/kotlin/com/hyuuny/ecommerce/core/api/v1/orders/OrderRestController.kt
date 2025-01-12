package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/orders")
@RestController
class OrderRestController(
    private val service: OrderService,
) {
    @PostMapping
    fun checkout(@RequestBody request: CheckoutRequestDto): ApiResponse<OrderViewResponseDto> {
        val newOrder = service.checkout(request.toCommand())
        return ApiResponse.success(OrderViewResponseDto(newOrder))
    }
}
