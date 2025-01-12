package com.hyuuny.ecommerce.storage.db.core.orders

enum class OrderStatus(private val description: String) {
    BEFORE_PAYMENT("결제 전"),
    COMPLETED_PAYMENT("결제 완료"),
    FAIL_PAYMENT("결제 실패"),
    PREPARING_DELIVERY("배송 준비"),
    BEFORE_SHIPPING("배송 전"),
    IN_SHIPPING("배송 중"),
    COMPLETED_SHIPPING("배송 완료"),
    CONFIRM_PURCHASE("구매 확정"),
}