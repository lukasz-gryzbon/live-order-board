package com.test.service.helper

import com.test.model.Order
import com.test.model.OrderSummary
import com.test.model.OrderType
import com.test.model.OrderType.SELL

class OrderSummaryService {
    // Alternatively, this method could return a list, but then the frontend would need to separate the SELL & BUY data
    fun summarise(allOrders: List<Order>): Map<OrderType, List<OrderSummary>> {
        val ordersByOrderType = allOrders.groupBy { it.orderType }

        val associateBy = OrderType.values().associateWith { orderType ->
            (ordersByOrderType[orderType] ?: emptyList())
                .groupingBy { order -> order.price }
                .fold(0.0) { totalQuantity, order -> totalQuantity + order.quantity }
                .map { priceToQuantity -> OrderSummary(priceToQuantity.value, priceToQuantity.key, orderType) }
                .sortedBy { summary -> if (summary.orderType == SELL) summary.price else -1 * summary.price }
        }

        return associateBy
    }
}
