package com.hyuuny.ecommerce.core.support.error

open class CoreException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)

class DuplicateEmailException(
    data: Any? = null
) : CoreException(ErrorType.DUPLICATE_EMAIL, data)

class UserNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.USER_NOT_FOUND, data)

class CategoryNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.CATEGORY_NOT_FOUND, data)

class BrandNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.BRAND_NOT_FOUND, data)

class ProductNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.PRODUCT_NOT_FOUND, data)

class InsufficientStockException(
    data: Any? = null,
) : CoreException(ErrorType.INSUFFICIENT_STOCK, data)

class OrderNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.ORDER_NOT_FOUND, data)

class OrderItemNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.ORDER_ITEM_NOT_FOUND, data)

class AlreadyCanceledOrderException :
    CoreException(ErrorType.ALREADY_CANCELED_ORDER_ITEM, "이미 취소된 주문 상품입니다.")

class AlreadyConfirmedPurchaseException :
    CoreException(ErrorType.ALREADY_CONFIRMED_PURCHASE, "이미 구매 확정된 주문 상품입니다.")

class InvalidConfirmPurchaseException(
    data: Any? = null,
) : CoreException(ErrorType.INVALID_CONFIRM_PURCHASE, data)

class InvalidCancelOrderItemException(
    data: Any? = null,
) : CoreException(ErrorType.INVALID_CANCEL_ORDER_ITEM, data)