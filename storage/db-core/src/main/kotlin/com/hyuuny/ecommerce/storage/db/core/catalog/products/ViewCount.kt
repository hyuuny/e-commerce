package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private const val ZERO_COUNT = 0

@Embeddable
data class ViewCount(
    @Column(nullable = false) val viewCount: Int
) {
    companion object {
        fun init(): ViewCount {
            return ViewCount(ZERO_COUNT)
        }
    }

    operator fun plus(additionalCount: Int): ViewCount {
        return ViewCount(viewCount + additionalCount)
    }
}