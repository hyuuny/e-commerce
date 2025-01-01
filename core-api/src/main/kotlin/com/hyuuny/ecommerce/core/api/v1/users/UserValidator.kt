package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.core.support.error.DuplicateEmailException
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import org.springframework.stereotype.Component

@Component
class UserValidator(
    private val repository: UserRepository,
) {
    fun validate(email: String) {
        if (repository.existsByEmail(email)) throw DuplicateEmailException("이미 존재하는 email입니다.")
    }
}