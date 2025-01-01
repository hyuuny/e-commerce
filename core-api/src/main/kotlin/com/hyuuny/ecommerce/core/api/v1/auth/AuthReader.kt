package com.hyuuny.ecommerce.core.api.v1.auth

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class AuthReader(
    private val authenticationManager: AuthenticationManager,
) {

    fun getAuthentication(username: String, password: String): Authentication =
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
}