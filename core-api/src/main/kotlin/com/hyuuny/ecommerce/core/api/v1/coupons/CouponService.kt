package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.api.v1.users.UserReader
import com.hyuuny.ecommerce.core.support.error.FailAcquiredLockException
import com.hyuuny.ecommerce.core.support.error.OverCouponMaxIssuanceCountException
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Transactional(readOnly = true)
@Service
class CouponService(
    private val redissonClient: RedissonClient,
    private val couponReader: CouponReader,
    private val couponWriter: CouponWriter,
    private val userReader: UserReader,
    private val userCouponReader: UserCouponReader,
) {

    companion object {
        private const val USER_COUPON_LOCK_KEY_TEMPLATE = "coupon:issue:%s:%s"
        private const val TRY_LOCK_TIME = 5L
        private const val OCCUPANCY_TIME = 3L
    }

    @Transactional
    fun issueCouponToUser(command: IssueUserCoupon): UserCouponEntity {
        val lockKey = String.format(USER_COUPON_LOCK_KEY_TEMPLATE, command.discountType.name, command.couponId)
        val lock: RLock = redissonClient.getLock(lockKey)

        try {
            val tryLock = lock.tryLock(TRY_LOCK_TIME, OCCUPANCY_TIME, TimeUnit.SECONDS)
            if (tryLock) {
                val coupon = couponReader.read(command.couponId)

                if (coupon.firstComeFirstServed) {
                    val currentCount = coupon.currentIssuedCount ?: 0
                    val maxCount = coupon.maxIssuanceCount ?: 0
                    if (currentCount >= maxCount) {
                        throw OverCouponMaxIssuanceCountException("쿠폰 최대 발행 횟수를 초과하였습니다. id: ${coupon.id}")
                    }
                    couponWriter.incrementIssuedCount(coupon.id)
                }

                val now = LocalDate.now()
                val newUserCoupon = command.toEntity(now, coupon.expiredDate)
                return couponWriter.write(newUserCoupon)
            } else {
                throw FailAcquiredLockException("쿠폰 발행을 위한 잠금을 획득하지 못했습니다.")
            }
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    fun getUserCoupon(userId: Long, couponId: Long): UserCouponData {
        val user = userReader.read(userId)
        val coupon = couponReader.read(couponId)
        val userCoupon = userCouponReader.read(user.id, coupon.id)
        return UserCouponData(userCoupon, coupon)
    }

}