package com.hyuuny.ecommerce.storage.db.core.reviews

import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewEntity, Long>, ReviewRepositoryCustom {
}
