package com.test.service.helper

import com.test.model.Order
import com.test.model.OrderSummary
import com.test.model.OrderType

class OrderSummaryService {
    // Alternatively, this method could return a list, but then the frontend would need to separate the SELL & BUY data
    fun summarise(allOrders: List<Order>?): Map<OrderType, List<OrderSummary>> {
        validate(allOrders)
        val ordersByOrderType = allOrders!!.groupBy { it.orderType }

        val associateBy = OrderType.values().associateWith { orderType ->
            (ordersByOrderType[orderType] ?: emptyList()).groupBy { order: Order -> order.price }
                .map { entry ->
                    OrderSummary(
                        entry.value.fold(0.0) { acc, order -> acc + order.quantity },
                        entry.key,
                        orderType
                    )
                }
                .sortedBy { summary: OrderSummary -> if (summary.orderType == OrderType.SELL) summary.price else -1 * summary.price }
        }


        return associateBy
    }

    private fun validate(orders: List<Order>?) {
        requireNotNull(orders) { MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE }
    }

    companion object {
        private const val MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE = "Missing argument: orders"
    }
}
