package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductBadgeReader
import com.hyuuny.ecommerce.core.api.v1.catalog.products.ProductReader
import com.hyuuny.ecommerce.core.api.v1.coupons.CouponReader
import com.hyuuny.ecommerce.core.api.v1.coupons.UserCouponReader
import com.hyuuny.ecommerce.core.api.v1.likes.LikeReader
import com.hyuuny.ecommerce.storage.db.core.coupons.PercentCouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.WonCouponEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class MyService(
    private val likeReader: LikeReader,
    private val productReader: ProductReader,
    private val productBadgeReader: ProductBadgeReader,
    private val userCouponReader: UserCouponReader,
    private val couponReader: CouponReader,
) {
    fun getAllLikedProducts(userId: Long, pageable: Pageable): SimplePage<MyLikedProductData> {
        val page = likeReader.search(userId, pageable)
        val likedProducts = productReader.readAllByIds(page.content.map { it.productId })
        val likedProductMap = likedProducts.associateBy { it.id }
        val badgeGroup = productBadgeReader.readAllByIds(likedProducts.map { it.id }).groupBy { it.productId }
        return SimplePage(page.content.mapNotNull { like ->
            val product = likedProductMap[like.productId] ?: return@mapNotNull null
            val badges = badgeGroup[product.id] ?: return@mapNotNull null
            MyLikedProductData(product, badges, true)
        }, page)
    }

    fun getAllUserCoupons(command: UserCouponSearchCommand, pageable: Pageable): SimplePage<UserCouponData> {
        val page = userCouponReader.search(command, pageable)
        val coupons = couponReader.readAllByIds(page.content.map { it.couponId })
        val couponMap = coupons.associateBy { it.id }
        return SimplePage(page.content.mapNotNull {
            val coupon = couponMap[it.couponId] ?: return@mapNotNull null

            when (coupon) {
                is WonCouponEntity -> UserCouponData(
                    entity = it,
                    coupon = coupon,
                    discountPrice = coupon.discountPrice
                )

                is PercentCouponEntity -> UserCouponData(
                    entity = it,
                    coupon = coupon,
                    discountPercent = coupon.discountPercent
                )

                else -> return@mapNotNull null
            }
        }, page)
    }
}
