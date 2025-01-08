package com.hyuuny.ecommerce.storage.db.core.catalog.products

enum class ProductStatus(private val description: String) {
    ON_SALE("판매 중"),
    STOPPED_SELLING("판매 중지"),
    SOLD_OUT("품절"),
}