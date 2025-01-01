package com.hyuuny.ecommerce.core.api.v1.users

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class UserService(
    private val userWriter: UserWriter,
    private val validator: UserValidator,
) {
    @Transactional
    fun signup(signupUser: SignupUser): UserData {
        validator.validate(signupUser.email)
        val newUser = userWriter.signup(signupUser)
        return UserData(newUser)
    }
}