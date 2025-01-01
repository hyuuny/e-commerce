package com.hyuuny.ecommerce.core.api.v1.users

import com.hyuuny.ecommerce.storage.db.core.users.UserEntity

data class UserData(
    val id: Long,
    val email: String,
    val name: String,
    val phoneNumber: String,
) {
    constructor(entity: UserEntity) : this(
        id = entity.id,
        email = entity.email,
        name = entity.name,
        phoneNumber = entity.phoneNumber,
    )
}