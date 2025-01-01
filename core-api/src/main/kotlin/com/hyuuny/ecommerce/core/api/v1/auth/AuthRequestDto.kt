package com.hyuuny.ecommerce.core.api.v1.auth

data class AuthRequestDto(
    val email: String,
    val password: String,
)