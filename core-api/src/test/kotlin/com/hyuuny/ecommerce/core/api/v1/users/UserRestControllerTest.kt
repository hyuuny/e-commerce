package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.error.ErrorCode
import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.response.ResultType
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

class UserRestControllerTest(
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
    fun `사용자는 회원가입을 할 수 있다`() {
        val request = SignupRequestDto(
            email = "newuser@naver.com",
            password = "password123",
            name = "나가입",
            phoneNumber = "01012345678",
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/users")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", notNullValue())
            body("data.email", equalTo(request.email))
            body("data.name", equalTo(request.name))
            body("data.phoneNumber", equalTo(request.phoneNumber))
            log().all()
        }
    }

    @Test
    fun `이미 가입된 이메일은 회원가입을 할 수 없다`() {
        val request = SignupRequestDto(
            email = DEFAULT_USER_EMAIL,
            password = "password123",
            name = "나가입",
            phoneNumber = "01012345678",
        )

        Given {
            contentType(ContentType.JSON)
            body(request)
            log().all()
        } When {
            post("/api/v1/users")
        } Then {
            statusCode(HttpStatus.SC_BAD_REQUEST)
            body("result", equalTo("ERROR"))
            body("data", equalTo(null))
            body("error.code", equalTo(ErrorCode.E100.name))
            body("error.message", equalTo(ErrorType.DUPLICATE_EMAIL_EXCEPTION.message))
            body("error.data", equalTo("이미 존재하는 email입니다."))
            log().all()
        }
    }
}