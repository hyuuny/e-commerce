package com.hyuuny.ecommerce.core.api.v1.orders

import java.time.LocalDateTime

data class OrderViewResponseDto(
    val id: Long,
    val orderCode: String,
    val userId: Long,
    val orderer: OrdererResponseDto,
    val deliveryDetail: DeliveryDetailResponseDto,
    val totalProductPrice: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalPrice: Long,
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
        totalProductPrice = orderView.totalProductPrice.totalProductAmount,
        totalDiscountAmount = orderView.totalDiscountAmount.totalDiscountAmount,
        shippingFee = orderView.shippingFee,
        totalPrice = orderView.totalPrice.totalAmount,
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
    val discountPrice: Long,
    val price: Long,
    val totalPrice: Long,
) {
    constructor(orderItemData: OrderItemData) : this(
        id = orderItemData.id,
        orderId = orderItemData.orderId,
        productId = orderItemData.productId,
        productName = orderItemData.productName,
        quantity = orderItemData.quantity,
        discountPrice = orderItemData.discountPrice.discountAmount,
        price = orderItemData.price.amount,
        totalPrice = orderItemData.totalPrice.totalAmount,
    )
}
