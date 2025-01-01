package com.hyuuny.ecommerce.core.security

import com.hyuuny.ecommerce.storage.db.core.users.Role
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class AuthUserDetails(
    private val user: UserEntity,
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        user.roles.map { role -> SimpleGrantedAuthority(role.name) }.toSet()

    override fun getPassword(): String = user.password

    override fun getUsername(): String = user.email

    fun getUserId(): Long = user.id

    fun getRoles(): Set<Role> = user.roles

}