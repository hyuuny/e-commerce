package com.hyuuny.ecommerce.storage.db.core.orders

import com.hyuuny.ecommerce.storage.db.core.response.SimplePage
import org.springframework.data.domain.Pageable

interface OrderRepositoryCustom {
    fun findAllBySearch(search: SearchOrder, pageable: Pageable): SimplePage<OrderEntity>
}
