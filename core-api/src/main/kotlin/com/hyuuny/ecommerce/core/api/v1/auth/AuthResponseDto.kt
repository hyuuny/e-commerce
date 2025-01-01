package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.storage.db.core.users.Role

data class UserWithTokenResponseDto(
    val id: Long,
    val email: String,
    val roles: Set<RoleResponseDto>,
    val token: String,
) {
    constructor(userWithTokenData: UserWithTokenData) : this(
        id = userWithTokenData.id,
        email = userWithTokenData.email,
        roles = userWithTokenData.roles.map { RoleResponseDto(it.role) }.toSet(),
        token = userWithTokenData.token,
    )
}

data class RoleResponseDto(
    val role: Role,
)