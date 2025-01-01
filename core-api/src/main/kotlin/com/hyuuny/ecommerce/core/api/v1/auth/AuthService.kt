package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.core.security.AuthUserDetails
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authReader: AuthReader,
    private val authWriter: AuthWriter,
) {
    fun auth(username: String, password: String): UserWithTokenData {
        val authentication = authReader.getAuthentication(username, password)
        val authUserDetails = authentication.principal as AuthUserDetails
        val token = authWriter.generateToken(authUserDetails)
        return UserWithTokenData(authUserDetails, token)
    }
}