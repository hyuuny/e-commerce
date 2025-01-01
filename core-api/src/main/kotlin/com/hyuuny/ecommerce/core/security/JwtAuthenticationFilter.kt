package com.hyuuny.ecommerce.core.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authUserDetailsService: AuthUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractToken(request)
        if (token != null && jwtTokenProvider.validateToken(token)) {
            val username = jwtTokenProvider.getUsernameFromToken(token)
            val userDetails = authUserDetailsService.loadUserByUsername(username)
            val authentication = createAuthentication(token, userDetails, request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    private fun createAuthentication(
        token: String,
        userDetails: UserDetails,
        request: HttpServletRequest
    ): UsernamePasswordAuthenticationToken {
        val authentication = jwtTokenProvider.getAuthentication(token, userDetails)
        return UsernamePasswordAuthenticationToken(
            authentication.principal,
            authentication.credentials,
            authentication.authorities
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }
    }
}


