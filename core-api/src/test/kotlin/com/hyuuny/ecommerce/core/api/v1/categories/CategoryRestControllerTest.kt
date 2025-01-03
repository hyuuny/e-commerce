package com.hyuuny.ecommerce.core.api.v1.categories

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryEntity
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryRepository
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

class CategoryRestControllerTest(
    @LocalServerPort val port: Int,
    private val repository: CategoryRepository,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
    }

    @Test
    fun `상위 카테고리 목록을 조회할 수 있다`() {
        val categories = listOf(
            CategoryEntity(null, "스킨케어", "categories/icons/skincare.png", false),
            CategoryEntity(null, "마스크팩", "categories/icons/mask-pack.png", false),
            CategoryEntity(null, "클렌징", "categories/icons/cleansing.png", false),
            CategoryEntity(null, "선케어", "categories/icons/sun-care.png", false),
            CategoryEntity(null, "메이크업", "categories/icons/makeup.png", false),
            CategoryEntity(null, "네일", "categories/icons/nail.png", false),
            CategoryEntity(null, "향수", "categories/icons/perfume.png", false),
        )
        repository.saveAll(categories)

        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/categories/parents")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data[0].name", equalTo(categories[0].name))
            body("data[1].name", equalTo(categories[1].name))
            body("data[2].name", equalTo(categories[2].name))
            body("data[3].name", equalTo(categories[3].name))
            body("data[4].name", equalTo(categories[4].name))
            body("data[5].name", equalTo(categories[5].name))
            body("data[6].name", equalTo(categories[6].name))
            log().all()
        }
    }
}