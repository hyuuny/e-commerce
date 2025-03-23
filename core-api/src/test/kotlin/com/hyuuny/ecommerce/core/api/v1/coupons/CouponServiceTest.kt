package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.api.v1.users.UserReader
import com.hyuuny.ecommerce.core.support.error.CouponNotFoundException
import com.hyuuny.ecommerce.core.support.error.ErrorType
import com.hyuuny.ecommerce.core.support.error.UserCouponNotFoundException
import com.hyuuny.ecommerce.core.support.error.UserNotFoundException
import com.hyuuny.ecommerce.storage.db.core.coupons.*
import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.redisson.api.RedissonClient
import java.time.LocalDate

class CouponServiceTest {
    private lateinit var redissonClient: RedissonClient
    private lateinit var couponReader: CouponReader
    private lateinit var couponWriter: CouponWriter
    private lateinit var userReader: UserReader
    private lateinit var userCouponReader: UserCouponReader
    private lateinit var couponService: CouponService

    @BeforeEach
    fun setUp() {
        redissonClient = mockk()
        couponReader = mockk()
        couponWriter = mockk()
        userReader = mockk()
        userCouponReader = mockk()
        couponService = CouponService(redissonClient, couponReader, couponWriter, userReader, userCouponReader)
    }

    @Test
    fun `회원은 자신의 쿠폰을 상세조회 할 수 있다`() {
        val userEntity = UserEntity("newuser@naver.com", "pwd123!", "홍길동", "01012345678", setOf(Role.CUSTOMER))
        val couponEntity = WonCouponEntity(
            couponType = CouponType.ALL_DISCOUNT,
            code = "5천원할인쿠폰",
            name = "5천원 할인 쿠폰입니다",
            expiredDate = LocalDate.now().plusDays(5),
            fromDate = LocalDate.now().minusDays(1),
            toDate = LocalDate.now().minusDays(6),
            minimumOrderPrice = MinimumOrderPrice(15000),
            maximumDiscountPrice = null,
            discountPrice = DiscountPrice(5000),
            firstComeFirstServed = false,
            maxIssuanceCount = null,
            currentIssuedCount = null,
        )
        val userCouponEntity = UserCouponEntity(
            userId = userEntity.id,
            couponId = couponEntity.id,
            publishedDate = LocalDate.now(),
            expiredDate = couponEntity.expiredDate,
            used = false,
        )
        every { userReader.read(any()) } returns userEntity
        every { couponReader.read(any()) } returns couponEntity
        every { userCouponReader.read(any(), any()) } returns userCouponEntity

        val userCoupon = couponService.getUserCoupon(userEntity.id, couponEntity.id)

        assertThat(userCoupon.userId).isEqualTo(userEntity.id)
        assertThat(userCoupon.couponId).isEqualTo(couponEntity.id)
        assertThat(userCoupon.couponType).isEqualTo(couponEntity.couponType)
        assertThat(userCoupon.couponName).isEqualTo(couponEntity.name)
        assertThat(userCoupon.publishedDate).isEqualTo(userCouponEntity.publishedDate)
        assertThat(userCoupon.fromDate).isEqualTo(couponEntity.fromDate)
        assertThat(userCoupon.toDate).isEqualTo(couponEntity.toDate)
        assertThat(userCoupon.minimumOrderPrice).isEqualTo(couponEntity.minimumOrderPrice)
        assertThat(userCoupon.maximumDiscountPrice).isEqualTo(couponEntity.maximumDiscountPrice)
        assertThat(userCoupon.used).isEqualTo(userCouponEntity.used)
        assertThat(userCoupon.usedDateTime).isEqualTo(userCouponEntity.usedDateTime)
    }

    @Test
    fun `존재하지 않는 회원은 자신의 쿠폰을 상세조회 할 수 없다`() {
        val invalidUserId = 99999L
        val couponEntity = WonCouponEntity(
            couponType = CouponType.ALL_DISCOUNT,
            code = "5천원할인쿠폰",
            name = "5천원 할인 쿠폰입니다",
            expiredDate = LocalDate.now().plusDays(5),
            fromDate = LocalDate.now().minusDays(1),
            toDate = LocalDate.now().minusDays(6),
            minimumOrderPrice = MinimumOrderPrice(15000),
            maximumDiscountPrice = null,
            discountPrice = DiscountPrice(5000),
            firstComeFirstServed = false,
            maxIssuanceCount = null,
            currentIssuedCount = null,
        )
        every { userReader.read(any()) } throws UserNotFoundException("회원을 찾을 수 없습니다. id: $invalidUserId")

        val exception = assertThrows<UserNotFoundException> {
            couponService.getUserCoupon(invalidUserId, couponEntity.id)
        }

        assertThat(exception.message).isEqualTo(ErrorType.USER_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("회원을 찾을 수 없습니다. id: $invalidUserId")
    }

    @Test
    fun `존재하지 않는 쿠폰으로 자신의 쿠폰을 상세조회 할 수 없다`() {
        val userEntity = UserEntity("newuser@naver.com", "pwd123!", "홍길동", "01012345678", setOf(Role.CUSTOMER))
        val invalidCouponId = 99999L
        every { userReader.read(any()) } returns userEntity
        every { couponReader.read(any()) } throws CouponNotFoundException("쿠폰을 찾을 수 없습니다. id: $invalidCouponId")

        val exception = assertThrows<CouponNotFoundException> {
            couponService.getUserCoupon(userEntity.id, invalidCouponId)
        }

        assertThat(exception.message).isEqualTo(ErrorType.COUPON_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("쿠폰을 찾을 수 없습니다. id: $invalidCouponId")
    }

    @Test
    fun `사용자는 발급받지 않은 쿠폰을 상세조회 할 수 없다`() {
        val userEntity = UserEntity("newuser@naver.com", "pwd123!", "홍길동", "01012345678", setOf(Role.CUSTOMER))
        val couponEntity = WonCouponEntity(
            couponType = CouponType.ALL_DISCOUNT,
            code = "5천원할인쿠폰",
            name = "5천원 할인 쿠폰입니다",
            expiredDate = LocalDate.now().plusDays(5),
            fromDate = LocalDate.now().minusDays(1),
            toDate = LocalDate.now().minusDays(6),
            minimumOrderPrice = MinimumOrderPrice(15000),
            maximumDiscountPrice = null,
            discountPrice = DiscountPrice(5000),
            firstComeFirstServed = false,
            maxIssuanceCount = null,
            currentIssuedCount = null,
        )
        every { userReader.read(any()) } returns userEntity
        every { couponReader.read(any()) } returns couponEntity
        every { userCouponReader.read(any(), any()) } throws
                UserCouponNotFoundException("${userEntity.id} 번 회원의 ${couponEntity.id} 번 쿠폰을 찾을 수 없습니다.")

        val exception = assertThrows<UserCouponNotFoundException> {
            couponService.getUserCoupon(userEntity.id, couponEntity.id)
        }

        assertThat(exception.message).isEqualTo(ErrorType.USER_COUPON_NOT_FOUND.message)
        assertThat(exception.data).isEqualTo("${userEntity.id} 번 회원의 ${couponEntity.id} 번 쿠폰을 찾을 수 없습니다.")
    }
}