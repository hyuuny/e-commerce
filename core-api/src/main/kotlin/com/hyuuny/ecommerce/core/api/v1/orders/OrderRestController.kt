package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
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

    @GetMapping
    fun search(
        request: OrderSearchRequestDto,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ApiResponse<SimplePage<OrderResponseDto>> {
        val page = service.search(request.toCommand(), pageable)
        return ApiResponse.success(SimplePage(page.content.map { OrderResponseDto(it) }, page))
    }

    @PatchMapping("/{id}/order-item/{orderItemId}/confirm")
    fun confirmPurchase(@PathVariable id: Long, @PathVariable orderItemId: Long): ApiResponse<Any> {
        service.confirmPurchase(id, orderItemId)
        return ApiResponse.success()
    }

    @PatchMapping("/{id}/order-item/{orderItemId}/cancel")
    fun cancel(@PathVariable id: Long, @PathVariable orderItemId: Long): ApiResponse<Any> {
        service.cancel(id, orderItemId)
        return ApiResponse.success()
    }
}
