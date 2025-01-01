package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ApiResponse<UserResponseDto> {
        val user = service.getUser(id)
        return ApiResponse.success(UserResponseDto(user))
    }
}