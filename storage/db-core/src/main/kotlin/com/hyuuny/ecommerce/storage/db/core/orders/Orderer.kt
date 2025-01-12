package com.hyuuny.ecommerce.storage.db.core.orders

import jakarta.persistence.Embeddable

@Embeddable
data class Orderer(
    val name: String,
    val phoneNumber: String,
)
