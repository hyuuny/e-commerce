package com.hyuuny.ecommerce.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    INVALID_AUTHENTICATION_EXCEPTION(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "invalid authentication", LogLevel.ERROR),
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, ErrorCode.E100, "duplicate email", LogLevel.ERROR),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, ErrorCode.E104, "user notFound", LogLevel.ERROR),
    CATEGORY_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, ErrorCode.E104, "category notFound", LogLevel.ERROR),
    BRAND_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, ErrorCode.E104, "brand notFound", LogLevel.ERROR),
}
