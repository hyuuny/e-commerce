package com.hyuuny.ecommerce.storage.db.core.coupons

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDate

@DiscriminatorValue("WON")
@Entity
class WonCouponEntity(
    couponType: CouponType,
    code: String,
    name: String,
    expiredDate: LocalDate,
    fromDate: LocalDate,
    toDate: LocalDate,
    minimumOrderPrice: MinimumOrderPrice,
    maximumDiscountPrice: MaximumDiscountPrice?,
    firstComeFirstServed: Boolean,
    maxIssuanceCount: Int?,
    currentIssuedCount: Int?,
    val discountPrice: DiscountPrice,
) : CouponEntity(
    couponType = couponType,
    code = code,
    name = name,
    expiredDate = expiredDate,
    fromDate = fromDate,
    toDate = toDate,
    minimumOrderPrice = minimumOrderPrice,
    firstComeFirstServed = firstComeFirstServed,
    maxIssuanceCount = maxIssuanceCount,
    currentIssuedCount = currentIssuedCount,
    maximumDiscountPrice = maximumDiscountPrice,
)
