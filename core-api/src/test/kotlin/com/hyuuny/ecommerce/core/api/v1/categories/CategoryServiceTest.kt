package com.hyuuny.ecommerce.core.api.v1.categories

import com.hyuuny.ecommerce.core.support.error.CategoryNotFoundException
import com.hyuuny.ecommerce.storage.db.core.categories.CategoryEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CategoryServiceTest {
    private lateinit var reader: CategoryReader
    private lateinit var service: CategoryService

    @BeforeEach
    fun setUp() {
        reader = mockk()
        service = CategoryService(reader)
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
        every { reader.readAllParentCategory() } returns categories

        val parentCategories = service.getAllParentCategory()

        assertThat(parentCategories).hasSize(categories.size)
        parentCategories.forEachIndexed { index, category ->
            assertThat(categories[index].parent).isNull()
            assertThat(categories[index].name).isEqualTo(category.name)
            assertThat(categories[index].iconImageUrl).isEqualTo(category.iconImageUrl)
            assertThat(categories[index].hide).isFalse()
        }
    }

    @Test
    fun `상위 카테고리의 하위 카테고리 목록을 조회할 수 있다`() {
        val parentCategory = CategoryEntity(null, "스킨케어", "categories/icons/skincare.png", false)
        val children = listOf(
            CategoryEntity(parentCategory, "스킨/토너"),
            CategoryEntity(parentCategory, "에센스/세럼/앰플"),
            CategoryEntity(parentCategory, "크림"),
            CategoryEntity(parentCategory, "로션"),
            CategoryEntity(parentCategory, "미스트/오일"),
            CategoryEntity(parentCategory, "스킨케어세트"),
        )
        every { reader.read(any()) } returns parentCategory
        every { reader.readAllChildrenCategory(any()) } returns children

        val childrenCategories = service.getAllChildrenCategory(parentCategory.id)

        assertThat(children).hasSize(children.size)
        childrenCategories.forEachIndexed { index, category ->
            assertThat(category.parentId).isEqualTo(parentCategory.id)
            assertThat(category.name).isEqualTo(children[index].name)
            assertThat(category.hide).isEqualTo(children[index].hide)
        }
    }

    @Test
    fun `존재하지 않는 카테고리로 하위 카테고리를 조회할 수 없다 `() {
        val invalidId = 9L
        every { reader.read(any()) } throws CategoryNotFoundException("카테고리를 찾을 수 없습니다. id: $invalidId")

        val exception = assertThrows<CategoryNotFoundException> {
            service.getAllChildrenCategory(invalidId)
        }

        assertThat(exception.message).isEqualTo("category notFound")
        assertThat(exception.data).isEqualTo("카테고리를 찾을 수 없습니다. id: $invalidId")
    }
}