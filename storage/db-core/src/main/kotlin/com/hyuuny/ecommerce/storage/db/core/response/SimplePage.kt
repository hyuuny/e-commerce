package com.hyuuny.ecommerce.storage.db.core.response

data class SimplePage<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val last: Boolean
) where T : Any {
    constructor(content: List<T>, page: SimplePage<*>) : this(content, page.page, page.size, page.last)

    constructor(page: Int, size: Int) : this(emptyList(), page, size, true)
}
