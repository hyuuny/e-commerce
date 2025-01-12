package com.hyuuny.ecommerce.core.support.error

open class CoreException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)

class DuplicateEmailException(
    data: Any? = null
) : CoreException(ErrorType.DUPLICATE_EMAIL_EXCEPTION, data)

class UserNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.USER_NOT_FOUND_EXCEPTION, data)

class CategoryNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.CATEGORY_NOT_FOUND_EXCEPTION, data)

class BrandNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.BRAND_NOT_FOUND_EXCEPTION, data)

class ProductNotFoundException(
    data: Any? = null,
) : CoreException(ErrorType.PRODUCT_NOT_FOUND_EXCEPTION, data)