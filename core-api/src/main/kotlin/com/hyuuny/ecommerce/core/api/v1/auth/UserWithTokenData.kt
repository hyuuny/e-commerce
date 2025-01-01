package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.core.security.AuthUserDetails
import com.hyuuny.ecommerce.storage.db.core.users.Role

data class UserWithTokenData(
    val id: Long,
    val email: String,
    val roles: Set<RoleData>,
    val token: String,
) {
    constructor(authUserDetails: AuthUserDetails, token: String) : this(
        id = authUserDetails.getUserId(),
        email = authUserDetails.username,
        roles = authUserDetails.authorities.map { RoleData(Role.valueOf(it.authority)) }.toSet(),
        token = token
    )
}

data class RoleData(
    val role: Role,
)
