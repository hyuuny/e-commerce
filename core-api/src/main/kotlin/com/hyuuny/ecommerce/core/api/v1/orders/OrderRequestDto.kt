package com.hyuuny.ecommerce.core.api.v1.orders

data class CheckoutRequestDto(
    val userId: Long,
    val orderer: OrdererRequestDto,
    val deliveryDetail: DeliveryDetailRequestDto,
    val totalProductAmount: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalAmount: Long,
    val items: List<CheckoutItemRequestDto>,
) {
    fun toCommand(): Checkout = Checkout(
        userId = userId,
        orderer = OrdererCommand(name = orderer.name, phoneNumber = orderer.phoneNumber),
        deliveryDetail = DeliveryDetailCommand(
            address = deliveryDetail.address,
            addressDetail = deliveryDetail.addressDetail,
            recipientName = deliveryDetail.recipientName,
            message = deliveryDetail.message
        ),
        totalProductAmount = totalProductAmount,
        totalDiscountAmount = totalDiscountAmount,
        shippingFee = shippingFee,
        totalAmount = totalAmount,
        items = items.map { it.toCommand() }
    )
}

data class OrdererRequestDto(
    val name: String,
    val phoneNumber: String,
)

data class DeliveryDetailRequestDto(
    val address: String,
    val addressDetail: String,
    val recipientName: String,
    val message: String,
)

data class CheckoutItemRequestDto(
    val productId: Long,
    val quantity: Long,
    val discountAmount: Long,
    val amount: Long,
    val totalAmount: Long,
) {
    fun toCommand(): CheckoutItem = CheckoutItem(
        productId = productId,
        quantity = quantity,
        discountAmount = discountAmount,
        amount = amount,
        totalAmount = totalAmount,
    )
}
