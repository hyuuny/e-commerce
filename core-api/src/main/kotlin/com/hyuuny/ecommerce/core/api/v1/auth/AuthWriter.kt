package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.core.security.AuthUserDetails
import com.hyuuny.ecommerce.core.security.JwtTokenProvider
import org.springframework.stereotype.Component

@Component
class AuthWriter(
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun generateToken(authUserDetails: AuthUserDetails): String = jwtTokenProvider.generateToken(
        authUserDetails.getUserId(),
        authUserDetails.username,
        authUserDetails.getRoles()
    )
}