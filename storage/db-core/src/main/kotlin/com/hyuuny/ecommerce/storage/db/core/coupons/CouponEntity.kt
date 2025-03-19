package com.hyuuny.ecommerce.storage.db.core.coupons

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discount_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "coupons")
@Entity
abstract class CouponEntity(
    @Enumerated(EnumType.STRING)
    val couponType: CouponType,
    val code: String,
    val name: String,
    val expiredDate: LocalDate,
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val minimumOrderPrice: MinimumOrderPrice,
    val maximumDiscountPrice: MaximumDiscountPrice?,
    val firstComeFirstServed: Boolean = false,
    val maxIssuanceCount: Int = 0,
    val currentIssuedCount: Int = 0,
) : BaseEntity() {
}