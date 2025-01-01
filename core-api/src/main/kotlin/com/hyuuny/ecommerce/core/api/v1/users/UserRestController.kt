package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/users")
@RestController
class UserRestController(
    private val service: UserService,
) {
    @PostMapping
    fun signup(@RequestBody request: SignupRequestDto): ApiResponse<UserResponseDto> {
        val newUser = service.signup(request.toSignupUser())
        return ApiResponse.success(UserResponseDto(newUser))
    }
}