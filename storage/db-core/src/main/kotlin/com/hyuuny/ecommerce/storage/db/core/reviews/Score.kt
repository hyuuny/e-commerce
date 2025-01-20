package com.hyuuny.ecommerce.storage.db.core.reviews

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private val SCORE_RANGE = setOf(1, 2, 3, 4, 5)

@Embeddable
data class Score(
    @Column(nullable = false) val score: Int,
) {
    init {
        require(score in SCORE_RANGE) { "평점은 1부터 5까지만 가능합니다." }
    }
}
