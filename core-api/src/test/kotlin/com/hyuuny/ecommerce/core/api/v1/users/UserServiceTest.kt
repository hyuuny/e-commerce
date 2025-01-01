package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserServiceTest {
    private lateinit var writer: UserWriter
    private lateinit var validator: UserValidator
    private lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        writer = mockk()
        validator = mockk()
        service = UserService(writer, validator)
    }

    @Test
    fun `사용자는 회원가입을 할 수 있다`() {
        val signupUser = SignupUser(
            email = "newuser@naver.com",
            password = "password123",
            name = "나가입",
            phoneNumber = "01012345678",
        )
        val encodedPassword = "encodedPassword123"
        val newUser = UserEntity(
            email = "newuser@naver.com",
            password = encodedPassword,
            name = "나가입",
            phoneNumber = "01012345678",
            roles = setOf(Role.CUSTOMER),
        )
        every { validator.validate(any()) } returns Unit
        every { writer.signup(any()) } returns newUser

        val userData = service.signup(signupUser)

        assertThat(userData.email).isEqualTo(signupUser.email)
        assertThat(userData.name).isEqualTo(signupUser.name)
        assertThat(userData.phoneNumber).isEqualTo(signupUser.phoneNumber)
    }

}