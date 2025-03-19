package com.hyuuny.ecommerce.storage.db.core.coupons

enum class CouponType(private val description: String) {
    ALL_DISCOUNT("모든 할인"),
    PRODUCT_DISCOUNT("상품 할인"),
    SHIPPING_DISCOUNT("배송비 할인")
}