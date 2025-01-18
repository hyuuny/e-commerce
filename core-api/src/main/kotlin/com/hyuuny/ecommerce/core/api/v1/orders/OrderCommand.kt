package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.*
import java.time.LocalDate

data class Checkout(
    val userId: Long,
    val orderer: OrdererCommand,
    val deliveryDetail: DeliveryDetailCommand,
    val totalProductAmount: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
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
        totalProductPrice = TotalProductPrice(totalProductAmount),
        totalDiscountPrice = TotalDiscountPrice(totalDiscountAmount),
        shippingFee = shippingFee,
        totalPrice = TotalPrice(totalAmount),
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
    val discountAmount: Long,
    val amount: Long,
    val totalAmount: Long,
) {
    fun toEntity(orderId: Long, productName: String): OrderItemEntity = OrderItemEntity(
        orderId = orderId,
        productId = productId,
        productName = productName,
        quantity = quantity,
        discountPrice = DiscountPrice(discountAmount),
        price = Price(amount),
        totalPrice = TotalPrice(totalAmount),
    )
}

data class OrderSearchCommand(
    val userId: Long,
    val status: OrderStatus? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
) {
    fun toSearch(): SearchOrder = SearchOrder(
        userId = userId,
        status = status,
        fromDate = fromDate,
        toDate = toDate,
    )
}

data class OrderItemCancelEvent(
    val orderId: Long,
)
