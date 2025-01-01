package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class UserWriter(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun signup(signupUser: SignupUser): UserEntity {
        val newUser = signupUser.toEntity(passwordEncoder.encode(signupUser.password))
        return repository.save(newUser)
    }
}