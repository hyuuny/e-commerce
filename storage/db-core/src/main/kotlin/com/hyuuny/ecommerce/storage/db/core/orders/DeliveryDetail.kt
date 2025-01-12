package com.hyuuny.ecommerce.storage.db.core.orders

import jakarta.persistence.Embeddable

@Embeddable
data class DeliveryDetail(
    val address: String,
    val addressDetail: String,
    val recipientName: String,
    val message: String,
)
