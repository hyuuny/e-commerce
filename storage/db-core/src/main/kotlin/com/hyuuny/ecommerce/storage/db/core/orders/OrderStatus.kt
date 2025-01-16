package com.hyuuny.ecommerce.storage.db.core.orders

enum class OrderStatus(private val description: String) {
    BEFORE_PAYMENT("결제 전"),
    COMPLETED_PAYMENT("결제 완료"),
    FAIL_PAYMENT("결제 실패"),
    PARTIAL_CANCELED("부분 취소"),
    ALL_CANCELED("취소"),
}