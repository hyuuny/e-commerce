package com.hyuuny.ecommerce.core.api.init

import com.hyuuny.ecommerce.core.BaseIntegrationTest.Companion.DEFAULT_USER_EMAIL
import com.hyuuny.ecommerce.core.BaseIntegrationTest.Companion.DEFAULT_USER_PASSWORD
import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class TestDataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val email = DEFAULT_USER_EMAIL
        val password = DEFAULT_USER_PASSWORD
        val userEntity = UserEntity(
            email = email,
            password = passwordEncoder.encode(password),
            name = "김성현",
            phoneNumber = "01012341234",
            roles = setOf(Role.CUSTOMER)
        )

        if (userRepository.findByEmail(email) == null) userRepository.save(userEntity)
    }
}
