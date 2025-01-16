package com.hyuuny.ecommerce.storage.db.core.orders

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*

@Table(
    name = "order_items",
    indexes = [Index(name = "idx_order_id", columnList = "order_id")]
)
@Entity
class OrderItemEntity(
    status: OrderItemStatus = OrderItemStatus.CREATED,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val quantity: Long,
    val discountPrice: DiscountPrice,
    val price: Price,
    val totalPrice: TotalPrice,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status = status
        protected set
}
