package com.hyuuny.ecommerce.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    INVALID_AUTHENTICATION_EXCEPTION(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "invalid authentication", LogLevel.ERROR),
    DUPLICATE_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST, ErrorCode.E100, "duplicate email", LogLevel.ERROR),
    DEFAULT_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        ErrorCode.E500,
        "An unexpected error has occurred.",
        LogLevel.ERROR
    ),
}
