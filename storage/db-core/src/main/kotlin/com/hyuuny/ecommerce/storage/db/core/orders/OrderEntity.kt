package com.hyuuny.ecommerce.storage.db.core.orders

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*

@Table(name = "orders")
@Entity
class OrderEntity(
    val orderCode: String,
    status: OrderStatus = OrderStatus.BEFORE_PAYMENT,
    val userId: Long,
    @Embedded val orderer: Orderer,
    @Embedded val deliveryDetail: DeliveryDetail,
    val totalProductPrice: TotalProductPrice,
    val totalDiscountPrice: TotalDiscountPrice,
    val shippingFee: Long,
    val totalPrice: TotalPrice,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    fun allCanceled() {
        this.status = OrderStatus.ALL_CANCELED
    }

    fun partialCanceled() {
        this.status = OrderStatus.PARTIAL_CANCELED
    }
}
