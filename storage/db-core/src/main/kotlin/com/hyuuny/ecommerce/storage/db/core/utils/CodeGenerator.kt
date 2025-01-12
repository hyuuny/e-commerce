package com.hyuuny.ecommerce.storage.db.core.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CodeGenerator {
    fun generateOrderCode(now: LocalDateTime = LocalDateTime.now()): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val formattedDateTime = formatter.format(now)
        return "O_$formattedDateTime"
    }
}
