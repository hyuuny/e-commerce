package com.hyuuny.ecommerce.storage.db.core.users

import com.hyuuny.ecommerce.storage.db.core.BaseEntity
import jakarta.persistence.*

@Table(name = "users")
@Entity
class UserEntity(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(EnumType.STRING)
    val roles: Set<Role> = hashSetOf()
) : BaseEntity()