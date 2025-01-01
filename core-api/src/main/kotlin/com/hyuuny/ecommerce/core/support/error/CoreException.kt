package com.hyuuny.ecommerce.core.support.error

open class CoreException(
    val errorType: ErrorType,
    val data: Any? = null,
) : RuntimeException(errorType.message)

class DuplicateEmailException(
    data: Any? = null
) : CoreException(ErrorType.DUPLICATE_EMAIL_EXCEPTION, data)

