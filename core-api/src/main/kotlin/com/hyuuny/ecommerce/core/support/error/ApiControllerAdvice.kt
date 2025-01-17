package com.hyuuny.ecommerce.core.support.error

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import com.hyuuny.ecommerce.logging.Log
import org.springframework.boot.logging.LogLevel
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {

    companion object : Log

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(e: CoreException): ResponseEntity<ApiResponse<Any>> {
        when (e.errorType.logLevel) {
            LogLevel.ERROR -> log.error("CoreException : {}", e.message, e)
            LogLevel.WARN -> log.warn("CoreException : {}", e.message, e)
            else -> log.info("CoreException : {}", e.message, e)
        }
        return ResponseEntity(ApiResponse.error(e.errorType, e.data), e.errorType.status)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleInvalidAuthenticationException(e: AuthenticationException): ResponseEntity<ApiResponse<Any>> {
        log.error("InvalidAuthenticationException : {}", e.message, e)
        return ResponseEntity(
            ApiResponse.error(ErrorType.INVALID_AUTHENTICATION),
            ErrorType.INVALID_AUTHENTICATION.status
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        log.error("Exception : {}", e.message, e)
        return ResponseEntity(
            ApiResponse.error(ErrorType.DEFAULT_ERROR),
            ErrorType.DEFAULT_ERROR.status
        )
    }
}
