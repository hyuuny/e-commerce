package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.core.support.error.UserNotFoundException
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserReader(
    private val repository: UserRepository,
) {
    fun read(id: Long): UserEntity = repository.findByIdOrNull(id)
        ?: throw UserNotFoundException("회원을 찾을 수 없습니다. id: $id")
}