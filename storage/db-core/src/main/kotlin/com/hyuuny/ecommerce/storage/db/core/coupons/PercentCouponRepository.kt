package com.hyuuny.ecommerce.storage.db.core.coupons

import org.springframework.data.jpa.repository.JpaRepository

interface PercentCouponRepository : JpaRepository<PercentCouponEntity, Long> {
}