package com.hyuuny.ecommerce.storage.db.core.utils

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.EntityPathBase
import com.querydsl.core.types.dsl.PathBuilder
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class QueryDslUtil {
    companion object {
        fun <T> getSort(pageable: Pageable, path: EntityPathBase<T>): Array<OrderSpecifier<*>> {
            val pathBuilder = PathBuilder(path::class.java, path.metadata.name)
            val orderSpecifiers = ArrayList<OrderSpecifier<*>>()

            for (sortOrder in pageable.sort) {
                val order = when (sortOrder.direction) {
                    Sort.Direction.ASC -> Order.ASC
                    Sort.Direction.DESC -> Order.DESC
                }

                val orderSpecifier = OrderSpecifier(order, pathBuilder.getString(sortOrder.property))
                orderSpecifiers.add(orderSpecifier)
            }
            return orderSpecifiers.toTypedArray()
        }
    }
}
