package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.api.v1.brands.BrandReader
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.*
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest

class ProductServiceTest {
    private lateinit var reader: ProductReader
    private lateinit var productBannerReader: ProductBannerReader
    private lateinit var productContentReader: ProductContentReader
    private lateinit var productBadgesReader: ProductBadgeReader
    private lateinit var brandReader: BrandReader
    private lateinit var service: ProductService

    @BeforeEach
    fun setUp() {
        reader = mockk()
        productBannerReader = mockk()
        productContentReader = mockk()
        productBadgesReader = mockk()
        brandReader = mockk()
        service = ProductService(reader, productBannerReader, productContentReader, productBadgesReader, brandReader)
    }

    @Test
    fun `상품 목록을 조회할 수 있다`() {
        val productEntities = listOf(
            ProductEntity(
                1, ON_SALE, "product-1", "thumbnail-1.png", Price(20000),
                DiscountPrice(1000), StockQuantity(100)
            ),
            ProductEntity(
                2, ON_SALE, "product-2", "thumbnail-2.png", Price(20000),
                DiscountPrice(2000), StockQuantity(200)
            ),
            ProductEntity(
                3, ON_SALE, "product-3", "thumbnail-3.png", Price(20000),
                DiscountPrice(3000), StockQuantity(300)
            ),
            ProductEntity(
                4, SOLD_OUT, "product-4", "thumbnail-4.png", Price(20000),
                DiscountPrice(4000), StockQuantity(300)
            ),
            ProductEntity(
                5, ON_SALE, "product-5", "thumbnail-5.png", Price(20000),
                DiscountPrice(5000), StockQuantity(300)
            ),
            ProductEntity(
                5, STOPPED_SELLING, "product-6", "thumbnail-6.png", Price(20000),
                DiscountPrice(6000), StockQuantity(300)
            ),
            ProductEntity(
                6, ON_SALE, "product-7", "thumbnail-7.png", Price(20000),
                DiscountPrice(7000), StockQuantity(300)
            ),
            ProductEntity(
                3, ON_SALE, "product-8", "thumbnail-3.png", Price(20000),
                DiscountPrice(8000), StockQuantity(300)
            ),
            ProductEntity(
                1, STOPPED_SELLING, "product-9", "thumbnail-9.png", Price(20000),
                DiscountPrice(9000), StockQuantity(300)
            ),
            ProductEntity(
                7, ON_SALE, "product-10", "thumbnail-10.png", Price(20000),
                DiscountPrice(10000), StockQuantity(300)
            ),
            ProductEntity(
                8, ON_SALE, "product-11", "thumbnail-11.png", Price(20000),
                DiscountPrice(11000), StockQuantity(300)
            ),
            ProductEntity(
                2, ON_SALE, "product-12", "thumbnail-12.png", Price(20000),
                DiscountPrice(12000), StockQuantity(300)
            ),
        )
        val badgeEntities = listOf(
            ProductBadgeEntity(1, productEntities.last().id, "오늘드림", "#FFC0CB", "#DCDCDC"),
            ProductBadgeEntity(2, productEntities.last().id, "BEST", "#565656", "#DCDCDC"),
            ProductBadgeEntity(3, productEntities.last().id, "증정", "#565656", "#DCDCDC"),
        )
        val page = SimplePage(productEntities.slice(0 until 10), 1, 10, false)
        every { reader.search(any(), any()) } returns page
        every { productBadgesReader.readAllByIds(any()) } returns badgeEntities

        val search = service.search(ProductSearchCommand(), PageRequest.of(0, 10))

        assertThat(search.content).hasSize(10)
        search.content.forEachIndexed { index, item ->
            assertThat(item.name).isEqualTo(productEntities[index].name)
        }
        assertThat(search.content.first().badges).hasSize(3)
    }
}