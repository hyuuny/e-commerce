package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ApiResponse<OrderViewResponseDto> {
        val order = service.getOrder(id)
        return ApiResponse.success(OrderViewResponseDto(order))
    }
}
