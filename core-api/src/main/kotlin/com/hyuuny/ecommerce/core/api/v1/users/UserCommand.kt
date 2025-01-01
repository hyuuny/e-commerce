package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity

data class SignupUser(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
) {
    fun toEntity(encodedPassword: String): UserEntity = UserEntity(
        email = email,
        password = encodedPassword,
        name = name,
        phoneNumber = phoneNumber,
        roles = setOf(Role.CUSTOMER),
    )
}