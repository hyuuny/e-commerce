package com.hyuuny.ecommerce.storage.db.core.orders

import java.time.LocalDate

data class SearchOrder(
    val userId: Long,
    val status: OrderStatus? = null,
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
)
