package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.core.BaseIntegrationTest
import com.hyuuny.ecommerce.core.TestContainer
import com.hyuuny.ecommerce.core.support.error.ErrorCode
import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.response.ResultType
import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity
import com.hyuuny.ecommerce.storage.db.core.brands.BrandRepository
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
import org.springframework.data.domain.PageRequest

@TestContainer
class BrandRestControllerTest(
    @LocalServerPort val port: Int,
    private val repository: BrandRepository,
    private val service: BrandService,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @AfterEach
    fun tearDown() {
        RestAssured.reset()
        repository.deleteAll()
        service.cacheEvict()
    }

    @Test
    fun `브랜드 목록을 조회할 수 있다`() {
        val brands = repository.saveAll(
            listOf(
                BrandEntity("메디힐", "mediheal", "brands/images/mediheal.png"),
                BrandEntity("토리든", "Torriden", "brands/images/Torriden.png"),
                BrandEntity("달바", "dalba", "brands/images/dalba.png"),
                BrandEntity("필리밀리", "Fillimilli", "brands/images/Fillimilli.png"),
                BrandEntity("닥터지", "Dr.G", "brands/images/Dr.G.png"),
                BrandEntity("라운드랩", "Round Lab", "brands/images/Round-Lab.png"),
                BrandEntity("어노브", "UNOVE", "brands/images/UNOVE.png"),
                BrandEntity("에스트라", "aesura", "brands/images/aesura.png"),
                BrandEntity("넘버즈인", "NIMBUZIN", "brands/images/NIMBUZIN.png"),
                BrandEntity("바이오더마", "BIODERMA", "brands/images/BIODERMA.png"),
            )
        )
        val pageable = PageRequest.of(0, 10)

        Given {
            contentType(ContentType.JSON)
            params("page", pageable.pageNumber)
            params("size", pageable.pageSize)
            params("sort", "id,DESC")
            log().all()
        } When {
            get("/api/v1/brands")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.content[0].nameKo", equalTo(brands[9].nameKo))
            body("data.content[1].nameKo", equalTo(brands[8].nameKo))
            body("data.content[2].nameKo", equalTo(brands[7].nameKo))
            body("data.content[3].nameKo", equalTo(brands[6].nameKo))
            body("data.content[4].nameKo", equalTo(brands[5].nameKo))
            body("data.content[5].nameKo", equalTo(brands[4].nameKo))
            body("data.content[6].nameKo", equalTo(brands[3].nameKo))
            body("data.content[7].nameKo", equalTo(brands[2].nameKo))
            body("data.content[8].nameKo", equalTo(brands[1].nameKo))
            body("data.content[9].nameKo", equalTo(brands[0].nameKo))
            body("data.page", equalTo(1))
            body("data.size", equalTo(10))
            body("data.last", equalTo(true))
            log().all()
        }
    }

    @Test
    fun `브랜드를 상세조회 할 수 있다`() {
        val brandEntity = BrandEntity("메디힐", "mediheal", "brands/images/mediheal.png")
        val savedBrand = repository.save(brandEntity)

        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/brands/${savedBrand.id}")
        } Then {
            statusCode(HttpStatus.SC_OK)
            body("result", equalTo(ResultType.SUCCESS.name))
            body("data.id", equalTo(savedBrand.id.toInt()))
            body("data.nameKo", equalTo(savedBrand.nameKo))
            body("data.nameEn", equalTo(savedBrand.nameEn))
            body("data.bannerImageUrl", equalTo(savedBrand.bannerImageUrl))
            log().all()
        }
    }

    @Test
    fun `존재하지 않는 카테고리를 상세조회 할 수 없다`() {
        Given {
            contentType(ContentType.JSON)
            log().all()
        } When {
            get("/api/v1/brands/$INVALID_ID")
        } Then {
            statusCode(HttpStatus.SC_NOT_FOUND)
            body("result", equalTo("ERROR"))
            body("data", equalTo(null))
            body("error.code", equalTo(ErrorCode.E404.name))
            body("error.message", equalTo(ErrorType.BRAND_NOT_FOUND_EXCEPTION.message))
            body("error.data", equalTo("브랜드를 찾을 수 없습니다. id: $INVALID_ID"))
            log().all()
        }
    }
}