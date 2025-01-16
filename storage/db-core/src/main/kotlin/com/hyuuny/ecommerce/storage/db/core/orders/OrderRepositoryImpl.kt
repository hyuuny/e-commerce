package com.hyuuny.ecommerce.storage.db.core.orders

import com.hyuuny.ecommerce.storage.db.core.orders.QOrderEntity.orderEntity
import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import com.hyuuny.ecommerce.storage.db.core.utils.QueryDslUtil
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import java.time.LocalTime
import kotlin.math.min

class OrderRepositoryImpl : OrderRepositoryCustom, QuerydslRepositorySupport(OrderEntity::class.java) {
    override fun findAllBySearch(search: SearchOrder, pageable: Pageable): SimplePage<OrderEntity> {
        var query = from(orderEntity)
            .where(orderEntity.userId.eq(search.userId))

        with(search) {
            status?.let { query = query.where(orderEntity.status.eq(it)) }
            fromDate?.atStartOfDay()?.let { start ->
                toDate?.atTime(LocalTime.MAX)?.let { end ->
                    query = query.where(orderEntity.createdAt.between(start, end))
                }
            }
        }
        query.orderBy(*QueryDslUtil.getSort(pageable, orderEntity))

        val size = pageable.pageSize
        val content = query.limit(size.toLong() + 1).offset(pageable.offset).fetch()

        val last = content.size <= size

        return SimplePage(content.slice(0 until min(content.size, size)), pageable.pageNumber + 1, size, last)
    }
}
