package com.hyuuny.ecommerce.storage.db.core.coupons

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CouponRepository : JpaRepository<CouponEntity, Long> {
    @Modifying
    @Query("UPDATE CouponEntity c SET c.currentIssuedCount = c.currentIssuedCount + 1 WHERE c.id = :id")
    fun incrementIssuedCount(id: Long): Int

    fun findByCode(code: String): CouponEntity?
}