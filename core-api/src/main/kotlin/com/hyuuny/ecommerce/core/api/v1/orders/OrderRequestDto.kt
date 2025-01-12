package com.hyuuny.ecommerce.core.api.v1.orders

data class CheckoutRequestDto(
    val userId: Long,
    val orderer: OrdererRequestDto,
    val deliveryDetail: DeliveryDetailRequestDto,
    val totalProductPrice: Long,
    val totalDiscountAmount: Long,
    val shippingFee: Long,
    val totalPrice: Long,
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
        totalProductPrice = totalProductPrice,
        totalDiscountAmount = totalDiscountAmount,
        shippingFee = shippingFee,
        totalPrice = totalPrice,
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
    val discountPrice: Long,
    val price: Long,
    val totalPrice: Long,
) {
    fun toCommand(): CheckoutItem = CheckoutItem(
        productId = productId,
        quantity = quantity,
        discountPrice = discountPrice,
        price = price,
        totalPrice = totalPrice,
    )
}
