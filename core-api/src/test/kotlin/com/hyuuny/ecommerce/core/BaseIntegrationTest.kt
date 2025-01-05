package com.hyuuny.ecommerce.core

import com.hyuuny.ecommerce.core.api.v1.auth.AuthRequestDto
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired

@TestEnvironment
abstract class BaseIntegrationTest {

    @Autowired
    lateinit var userRepository: UserRepository

    companion object {
        const val DEFAULT_USER_EMAIL = "firstuser@naver.com"
        const val DEFAULT_USER_PASSWORD = "ab123345!"
    }

    fun generateJwtToken(email: String, password: String): String = "Bearer " + (Given {
        contentType(ContentType.JSON)
        body(AuthRequestDto(email, password))
        log().all()
    } When {
        post("/api/v1/auth")
    } Then {
        statusCode(HttpStatus.SC_OK)
    }).extract().path("data.token")

    fun deleteAllUser() = userRepository.findAll().filterNot { it.email == DEFAULT_USER_EMAIL }
        .forEach { userRepository.delete(it) }

}