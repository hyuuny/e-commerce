package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/auth")
@RestController
class AuthRestController(
    private val service: AuthService,
) {
    @PostMapping
    fun auth(@RequestBody request: AuthRequestDto): ApiResponse<UserWithTokenResponseDto> {
        val userWithToken = service.auth(request.email, request.password)
        return ApiResponse.success(UserWithTokenResponseDto(userWithToken))
    }

}