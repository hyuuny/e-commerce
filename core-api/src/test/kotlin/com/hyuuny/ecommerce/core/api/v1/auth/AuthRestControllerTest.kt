package com.hyuuny.ecommerce.core.api.v1.auth

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.users.Role
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

class AuthRestControllerTest(
    @LocalServerPort private val port: Int,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        deleteAllUser()
    }

    @Test
    fun `로그인에 성공하면 JWT 토큰이 발급된다`() {
        val request = AuthRequestDto(DEFAULT_USER_EMAIL, DEFAULT_USER_PASSWORD)

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/auth")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", notNullValue())
            body("data.email", equalTo(DEFAULT_USER_EMAIL))
            body("data.roles[0].role", equalTo(Role.CUSTOMER.name))
            body("data.token", notNullValue())
            log().all()
        }
    }

}