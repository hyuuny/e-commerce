package com.hyuuny.ecommerce.storage.db.core.coupons

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Table(
    name = "user_coupons",
    indexes = [Index(name = "idx_user_id", columnList = "user_id")]
)
@Entity
class UserCouponEntity(
    val userId: Long,
    val couponId: Long,
    val publishedDate: LocalDate,
    val expiredDate: LocalDate,
    val used: Boolean = false,
    val usedDateTime: LocalDateTime? = null,
) : BaseEntity() {
}