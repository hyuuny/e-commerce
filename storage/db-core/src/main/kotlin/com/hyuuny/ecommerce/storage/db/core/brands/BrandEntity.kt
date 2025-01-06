package com.hyuuny.ecommerce.storage.db.core.brands

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "brands")
@Entity
class BrandEntity(
    val nameKo: String,
    val nameEn: String,
    val bannerImageUrl: String,
) : BaseEntity()
