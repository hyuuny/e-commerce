package com.hyuuny.ecommerce.core.api.v1.orders

import java.time.LocalDateTime

data class OrderViewResponseDto(
    val id: Long,
    val orderCode: String,
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
        orderId = orderItemData.orderId,
        productId = orderItemData.productId,
        productName = orderItemData.productName,
        quantity = orderItemData.quantity,
        discountAmount = orderItemData.discountPrice.discountAmount,
        amount = orderItemData.price.amount,
        totalAmount = orderItemData.totalPrice.totalAmount,
    )
}
