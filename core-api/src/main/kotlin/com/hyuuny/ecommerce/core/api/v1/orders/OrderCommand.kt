package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.*

data class Checkout(
    val userId: Long,
    val orderer: OrdererCommand,
    val deliveryDetail: DeliveryDetailCommand,
    val totalProductPrice: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalPrice: Long,
    val items: List<CheckoutItem>,
) {
    fun toEntity(orderCode: String): OrderEntity = OrderEntity(
        userId = userId,
        orderCode = orderCode,
        orderer = Orderer(name = orderer.name, phoneNumber = orderer.phoneNumber),
        deliveryDetail = DeliveryDetail(
            address = deliveryDetail.address,
            addressDetail = deliveryDetail.addressDetail,
            recipientName = deliveryDetail.recipientName,
            message = deliveryDetail.message
        ),
        totalProductPrice = TotalProductPrice(totalProductPrice),
        totalDiscountAmount = TotalDiscountPrice(totalDiscountAmount),
        shippingFee = shippingFee,
        totalPrice = TotalPrice(totalPrice),
    )
}

data class OrdererCommand(
    val name: String,
    val phoneNumber: String,
)

data class DeliveryDetailCommand(
    val address: String,
    val addressDetail: String,
    val recipientName: String,
    val message: String,
)

data class CheckoutItem(
    val productId: Long,
    val quantity: Long,
    val discountPrice: Long,
    val price: Long,
    val totalPrice: Long,
) {
    fun toEntity(orderId: Long, productName: String): OrderItemEntity = OrderItemEntity(
        orderId = orderId,
        productId = productId,
        productName = productName,
        quantity = quantity,
        discountPrice = DiscountPrice(discountPrice),
        price = Price(price),
        totalPrice = TotalPrice(totalPrice),
    )
}
