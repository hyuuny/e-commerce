package com.hyuuny.ecommerce.core.api.v1.users

data class UserResponseDto(
    val id: Long,
    val email: String,
    val name: String,
    val phoneNumber: String,
) {
    constructor(userData: UserData) : this(
        id = userData.id,
        email = userData.email,
        name = userData.name,
        phoneNumber = userData.phoneNumber,
    )
}