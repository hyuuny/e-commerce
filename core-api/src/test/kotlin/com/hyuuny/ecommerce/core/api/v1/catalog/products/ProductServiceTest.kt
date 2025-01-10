package com.hyuuny.ecommerce.core.api.v1.catalog.products

import com.hyuuny.ecommerce.core.api.v1.brands.BrandReader
import com.hyuuny.ecommerce.core.support.error.ProductNotFoundException
import com.hyuuny.ecommerce.storage.db.core.brands.BrandEntity
import com.hyuuny.ecommerce.storage.db.core.catalog.products.*
import com.hyuuny.ecommerce.storage.db.core.catalog.products.ProductStatus.*
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun `상품을 상세조회 할 수 있다`() {
        val brandEntity = BrandEntity("메디힐", "mediheal", "brands/images/mediheal.png")
        val productEntity = ProductEntity(
            brandId = brandEntity.id,
            name = "[1월올영픽/대용량200ml] 웰라쥬 리얼 히알루로닉 블루 100 앰플 100ml 리필 기획 (+마스크1매)",
            thumbnailUrl = "products/thumbnail/wellage.png",
            price = Price(33000),
            discountPrice = DiscountPrice(6600),
            stockQuantity = StockQuantity(1_000)
        )
        val bannerEntities = listOf(
            ProductBannerEntity(1, productEntity.id, "banner1.png"),
            ProductBannerEntity(2, productEntity.id, "banner2.png"),
            ProductBannerEntity(3, productEntity.id, "banner3.png"),
            ProductBannerEntity(4, productEntity.id, "banner4.png"),
        )
        val contentEntities = listOf(
            ProductContentEntity(1, productEntity.id, "content1.png"),
            ProductContentEntity(2, productEntity.id, "content2.png"),
            ProductContentEntity(3, productEntity.id, "content3.png"),
            ProductContentEntity(4, productEntity.id, "content4.png"),
            ProductContentEntity(5, productEntity.id, "content5.png"),
            ProductContentEntity(6, productEntity.id, "content6.png"),
            ProductContentEntity(7, productEntity.id, "content7.png"),
            ProductContentEntity(8, productEntity.id, "content8.png"),
            ProductContentEntity(9, productEntity.id, "content9.png"),
            ProductContentEntity(10, productEntity.id, "content10.png"),
        )
        val badgeEntities = listOf(
            ProductBadgeEntity(1, productEntity.id, "오늘드림", "#FFC0CB", "#DCDCDC"),
            ProductBadgeEntity(2, productEntity.id, "BEST", "#565656", "#DCDCDC"),
        )
        every { reader.read(any()) } returns productEntity
        every { brandReader.read(any()) } returns brandEntity
        every { productBannerReader.readAll(any()) } returns bannerEntities
        every { productContentReader.readAll(any()) } returns contentEntities
        every { productBadgesReader.readAll(any()) } returns badgeEntities

        val product = service.getProduct(productEntity.id)

        assertThat(product.brandId).isEqualTo(brandEntity.id)
        assertThat(product.status).isEqualTo(productEntity.status)
        assertThat(product.name).isEqualTo(productEntity.name)
        assertThat(product.thumbnailUrl).isEqualTo(productEntity.thumbnailUrl)
        assertThat(product.price).isEqualTo(productEntity.price)
        assertThat(product.discountPrice).isEqualTo(productEntity.discountPrice)
        assertThat(product.stockQuantity).isEqualTo(productEntity.stockQuantity)
        product.banners.forEachIndexed { index, banner ->
            assertThat(banner.productId).isEqualTo(bannerEntities[index].productId)
            assertThat(banner.imageUrl).isEqualTo(bannerEntities[index].imageUrl)
        }
        product.contents.forEachIndexed { index, content ->
            assertThat(content.productId).isEqualTo(contentEntities[index].productId)
            assertThat(content.imageUrl).isEqualTo(contentEntities[index].imageUrl)
        }
        product.badges.forEachIndexed { index, badge ->
            assertThat(badge.productId).isEqualTo(badgeEntities[index].productId)
            assertThat(badge.title).isEqualTo(badgeEntities[index].title)
            assertThat(badge.color).isEqualTo(badgeEntities[index].color)
            assertThat(badge.bgColor).isEqualTo(badgeEntities[index].bgColor)
        }
    }

    @Test
    fun `존재하지 않는 상품을 상세조회 할 수 없다`() {
        val invalidId = 9L
        every { reader.read(any()) } throws ProductNotFoundException("상품을 찾을 수 없습니다 id: $invalidId")

        val exception = assertThrows<ProductNotFoundException> {
            service.getProduct(invalidId)
        }

        assertThat(exception.message).isEqualTo("product notFound")
        assertThat(exception.data).isEqualTo("상품을 찾을 수 없습니다 id: $invalidId")
    }
}