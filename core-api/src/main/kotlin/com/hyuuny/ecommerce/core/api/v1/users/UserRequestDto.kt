package com.hyuuny.ecommerce.core.api.v1.users

data class SignupRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
) {
    fun toSignupUser(): SignupUser = SignupUser(
        email = email,
        password = password,
        name = name,
        phoneNumber = phoneNumber,
    )
}