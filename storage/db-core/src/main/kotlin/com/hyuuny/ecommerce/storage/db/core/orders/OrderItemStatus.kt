package com.hyuuny.ecommerce.storage.db.core.orders

enum class OrderItemStatus(private val description: String) {
    CREATED("생성"),
    PREPARING_DELIVERY("배송 준비"),
    IN_SHIPPING("배송 중"),
    COMPLETED_SHIPPING("배송 완료"),
    CONFIRM_PURCHASE("구매 확정"),
    CANCELED("취소");

    fun satisfyConfirmPurchase(): Boolean = setOf(IN_SHIPPING, COMPLETED_SHIPPING).contains(this)

    fun satisfyCancel(): Boolean = setOf(CREATED, PREPARING_DELIVERY).contains(this)
}
