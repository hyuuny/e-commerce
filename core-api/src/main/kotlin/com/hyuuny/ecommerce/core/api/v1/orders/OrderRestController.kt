package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.core.support.utils.ExcelUtil
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import jakarta.servlet.http.HttpServletResponse
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

    @GetMapping("/users/{userId}/excel/download")
    fun downloadUserOrdersExcel(
        @PathVariable userId: Long,
        response: HttpServletResponse
    ) {
        val orders = service.getAllOrders(userId)
        val headers = listOf(
            "주문 아이디", "주문 코드", "상태", "회원명",
            "총 상품 가격", "할인 금액", "배송비", "총 결제 금액",
        )
        val data = orders.map { order ->
            listOf(
                order.id,
                order.orderCode,
                order.status.name,
                order.ordererName,
                order.totalProductPrice.totalProductAmount,
                order.totalDiscountPrice.totalDiscountAmount,
                order.shippingFee,
                order.totalPrice.totalAmount
            )
        }

        ExcelUtil.downloadExcel(
            response,
            fileName = "user_${userId}_orders",
            sheetName = "주문 내역",
            headers = headers,
            data = data
        )
    }
}
