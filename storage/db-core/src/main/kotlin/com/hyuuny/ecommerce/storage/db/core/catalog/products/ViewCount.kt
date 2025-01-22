package com.hyuuny.ecommerce.storage.db.core.catalog.products

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class ViewCount(
    @Column(nullable = false) val viewCount: Int
) {
    operator fun plus(additionalCount: Int): ViewCount {
        return ViewCount(viewCount + additionalCount)
    }
}