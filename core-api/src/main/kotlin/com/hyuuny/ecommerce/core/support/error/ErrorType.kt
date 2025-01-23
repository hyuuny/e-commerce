package com.hyuuny.ecommerce.core.support.error

import org.springframework.boot.logging.LogLevel
import org.springframework.http.HttpStatus

enum class ErrorType(val status: HttpStatus, val code: ErrorCode, val message: String, val logLevel: LogLevel) {
    DEFAULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.E500, "An unexpected error has occurred.", LogLevel.ERROR),
    INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, ErrorCode.E401, "invalid authentication", LogLevel.ERROR),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, ErrorCode.E400, "duplicate email", LogLevel.ERROR),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "user notFound", LogLevel.ERROR),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "brand notFound", LogLevel.ERROR),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "category notFound", LogLevel.ERROR),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "product notFound", LogLevel.ERROR),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, ErrorCode.E400, "insufficient stock", LogLevel.ERROR),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "order notFound", LogLevel.ERROR),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "orderItem notFound", LogLevel.ERROR),
    ALREADY_CANCELED_ORDER_ITEM(HttpStatus.BAD_REQUEST, ErrorCode.E400, "already canceled orderItem", LogLevel.ERROR),
    ALREADY_CONFIRMED_PURCHASE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "already confirmedPurchase", LogLevel.ERROR),
    INVALID_CONFIRM_PURCHASE(HttpStatus.BAD_REQUEST, ErrorCode.E400, "invalid confirmPurchase", LogLevel.ERROR),
    INVALID_CANCEL_ORDER_ITEM(HttpStatus.BAD_REQUEST, ErrorCode.E400, "invalid cancelOrderItem", LogLevel.ERROR),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCode.E404, "review notFound", LogLevel.ERROR),
}
