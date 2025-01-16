package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.OrderItemStatus
import com.hyuuny.ecommerce.storage.db.core.orders.OrderStatus
import java.time.LocalDateTime

data class OrderViewResponseDto(
    val id: Long,
    val orderCode: String,
    val status: OrderStatus,
    val userId: Long,
    val orderer: OrdererResponseDto,
    val deliveryDetail: DeliveryDetailResponseDto,
    val totalProductAmount: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
    val items: List<OrderItemResponseDto>,
    val createdAt: LocalDateTime,
) {
    constructor(orderView: OrderView) : this(
        id = orderView.id,
        orderCode = orderView.orderCode,
        status = orderView.status,
        userId = orderView.userId,
        orderer = OrdererResponseDto(
            name = orderView.orderer.name,
            phoneNumber = orderView.orderer.phoneNumber,
        ),
        deliveryDetail = DeliveryDetailResponseDto(
            address = orderView.deliveryDetailData.address,
            addressDetail = orderView.deliveryDetailData.addressDetail,
            recipientName = orderView.deliveryDetailData.recipientName,
            message = orderView.deliveryDetailData.message,
        ),
        totalProductAmount = orderView.totalProductPrice.totalProductAmount,
        totalDiscountAmount = orderView.totalDiscountPrice.totalDiscountAmount,
        shippingFee = orderView.shippingFee,
        totalAmount = orderView.totalPrice.totalAmount,
        items = orderView.items.map { OrderItemResponseDto(it) },
        createdAt = orderView.createdAt,
    )
}

data class OrdererResponseDto(
    val name: String,
    val phoneNumber: String
)

data class DeliveryDetailResponseDto(
    val address: String,
    val addressDetail: String,
    val recipientName: String,
    val message: String,
)

data class OrderItemResponseDto(
    val id: Long,
    val status: OrderItemStatus,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Long,
    val discountAmount: Long,
    val amount: Long,
    val totalAmount: Long,
) {
    constructor(orderItemData: OrderItemData) : this(
        id = orderItemData.id,
        status = orderItemData.status,
        orderId = orderItemData.orderId,
        productId = orderItemData.productId,
        productName = orderItemData.productName,
        quantity = orderItemData.quantity,
        discountAmount = orderItemData.discountPrice.discountAmount,
        amount = orderItemData.price.amount,
        totalAmount = orderItemData.totalPrice.totalAmount,
    )
}

data class OrderResponseDto(
    val id: Long,
    val orderCode: String,
    val status: OrderStatus,
    val userId: Long,
    val totalProductAmount: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
    val items: List<OrderItemResponseDto>,
) {
    constructor(orderData: OrderData) : this(
        id = orderData.id,
        orderCode = orderData.orderCode,
        status = orderData.status,
        userId = orderData.userId,
        totalProductAmount = orderData.totalProductPrice.totalProductAmount,
        totalDiscountAmount = orderData.totalDiscountPrice.totalDiscountAmount,
        shippingFee = orderData.shippingFee,
        totalAmount = orderData.totalPrice.totalAmount,
        items = orderData.items.map { OrderItemResponseDto(it) },
    )
}
