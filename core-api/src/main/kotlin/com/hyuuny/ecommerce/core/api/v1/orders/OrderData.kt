package com.hyuuny.ecommerce.core.api.v1.orders

import com.hyuuny.ecommerce.storage.db.core.orders.*
import java.time.LocalDateTime

data class OrderView(
    val id: Long,
    val orderCode: String,
    val status: OrderStatus,
    val userId: Long,
    val orderer: OrdererData,
    val deliveryDetailData: DeliveryDetailData,
    val totalProductPrice: TotalProductPrice,
    val totalDiscountPrice: TotalDiscountPrice,
    val shippingFee: Long,
    val totalPrice: TotalPrice,
    val items: List<OrderItemData>,
    val createdAt: LocalDateTime,
) {
    constructor(entity: OrderEntity, items: List<OrderItemEntity>) : this(
        id = entity.id,
        orderCode = entity.orderCode,
        status = entity.status,
        userId = entity.userId,
        orderer = OrdererData(
            name = entity.orderer.name,
            phoneNumber = entity.orderer.phoneNumber,
        ),
        deliveryDetailData = DeliveryDetailData(
            address = entity.deliveryDetail.address,
            addressDetail = entity.deliveryDetail.addressDetail,
            recipientName = entity.deliveryDetail.recipientName,
            message = entity.deliveryDetail.message,
        ),
        totalProductPrice = entity.totalProductPrice,
        totalDiscountPrice = entity.totalDiscountPrice,
        shippingFee = entity.shippingFee,
        totalPrice = entity.totalPrice,
        items = items.map { OrderItemData(it) },
        createdAt = entity.createdAt,
    )
}

data class OrdererData(
    val name: String,
    val phoneNumber: String
)

data class DeliveryDetailData(
    val address: String,
    val addressDetail: String,
    val recipientName: String,
    val message: String,
)

data class OrderItemData(
    val id: Long,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Long,
    val discountPrice: DiscountPrice,
    val price: Price,
    val totalPrice: TotalPrice,
) {
    constructor(orderItemEntity: OrderItemEntity) : this(
        id = orderItemEntity.id,
        orderId = orderItemEntity.orderId,
        productId = orderItemEntity.productId,
        productName = orderItemEntity.productName,
        quantity = orderItemEntity.quantity,
        discountPrice = DiscountPrice(orderItemEntity.discountPrice.discountAmount),
        price = Price(orderItemEntity.price.amount),
        totalPrice = TotalPrice(orderItemEntity.totalPrice.totalAmount),
    )
}

data class OrderData(
    val id: Long,
    val orderCode: String,
    val status: OrderStatus,
    val userId: Long,
    val totalProductPrice: TotalProductPrice,
    val totalDiscountPrice: TotalDiscountPrice,
    val shippingFee: Long,
    val totalPrice: TotalPrice,
    val items: List<OrderItemData>,
) {
    constructor(entity: OrderEntity, items: List<OrderItemEntity>) : this(
        id = entity.id,
        orderCode = entity.orderCode,
        status = entity.status,
        userId = entity.userId,
        totalProductPrice = entity.totalProductPrice,
        totalDiscountPrice = entity.totalDiscountPrice,
        shippingFee = entity.shippingFee,
        totalPrice = entity.totalPrice,
        items = items.map { OrderItemData(it) },
    )
}
