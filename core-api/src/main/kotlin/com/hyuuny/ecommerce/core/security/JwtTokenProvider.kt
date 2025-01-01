package com.hyuuny.ecommerce.core.security

import com.hyuuny.ecommerce.storage.db.core.users.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}") private val secretKey: String,
    @Value("\${jwt.access-expiration-time}") private val accessTokenExpirationTime: Long
) {

    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun generateToken(userId: Long, email: String, roles: Set<Role>): String {
        val claims: Map<String, Any> = mapOf(
            "userId" to userId,
            "roles" to roles.map { it.name }
        )
        val now = Date()
        val accessTime = Duration.ofMinutes(accessTokenExpirationTime).toMillis()
        val expiryDate = Date(now.time + accessTime)

        return Jwts.builder()
            .subject(email)
            .claims(claims)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }

    fun getUsernameFromToken(token: String): String = getClaimsFromToken(token).subject

    fun validateToken(token: String): Boolean = try {
        getClaimsFromToken(token)
        true
    } catch (e: Exception) {
        false
    }

    fun getAuthentication(token: String, userDetails: UserDetails): Authentication =
        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

    private fun getClaimsFromToken(token: String): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
}


