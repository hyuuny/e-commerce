package com.hyuuny.ecommerce.core.api.v1.brands

import com.hyuuny.ecommerce.core.support.error.BrandNotFoundException
import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest

class BrandServiceTest {
    private lateinit var reader: BrandReader
    private lateinit var service: BrandService

    @BeforeEach
    fun setUp() {
        reader = mockk()
        service = BrandService(reader)
        service.cacheEvict()
    }

    @Test
    fun `브랜드 목록을 조회할 수 있다`() {
        val brands = listOf(
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
        val page = SimplePage(brands, 1, 10, true)
        every { reader.findAllBySearch(any(), any()) } returns page

        val search = service.search(BrandSearchCommand(), PageRequest.of(0, 10))

        assertThat(search.content).hasSize(10)
        search.content.forEachIndexed { index, item ->
            assertThat(item.nameKo).isEqualTo(brands[index].nameKo)
            assertThat(item.nameEn).isEqualTo(brands[index].nameEn)
        }
    }

    @Test
    fun `브랜드 이름으로 검색할 수 있다`() {
        val brands = listOf(
            BrandEntity("닥터지", "Dr.G", "brands/images/Dr.G.png"),
        )
        val page = SimplePage(brands, 1, 10, true)
        every { reader.findAllBySearch(any(), any()) } returns page

        val search = service.search(BrandSearchCommand("닥터"), PageRequest.of(0, 10))

        assertThat(search.content).hasSize(1)
        search.content.forEachIndexed { index, item ->
            assertThat(item.nameKo).isEqualTo(brands[index].nameKo)
            assertThat(item.nameEn).isEqualTo(brands[index].nameEn)
        }
    }

    @Test
    fun `브랜드를 상세조회 할 수 있다`() {
        val brandEntity = BrandEntity("메디힐", "mediheal", "brands/images/mediheal.png")
        every { reader.read(any()) } returns brandEntity

        val brandDetailData = service.getBrand(brandEntity.id)

        assertThat(brandDetailData.nameKo).isEqualTo(brandEntity.nameKo)
        assertThat(brandDetailData.nameEn).isEqualTo(brandEntity.nameEn)
        assertThat(brandDetailData.bannerImageUrl).isEqualTo(brandEntity.bannerImageUrl)
    }

    @Test
    fun `존재하지 않는 카테고리를 상세조회 할 수 없다`() {
        val invalidId = 9L
        every { reader.read(any()) } throws BrandNotFoundException("브랜드를 찾을 수 없습니다. id: $invalidId")

        val exception = org.junit.jupiter.api.assertThrows<BrandNotFoundException> {
            service.getBrand(invalidId)
        }

        assertThat(exception.message).isEqualTo("brand notFound")
        assertThat(exception.data).isEqualTo("브랜드를 찾을 수 없습니다. id: $invalidId")
    }
}