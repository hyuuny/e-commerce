package com.hyuuny.ecommerce.storage.db.core.users

enum class Role(private val description: String) {
    ADMIN("관리자"),
    CUSTOMER("고객"),
}