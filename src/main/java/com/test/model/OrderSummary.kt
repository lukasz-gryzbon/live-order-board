package com.test.model

data class OrderSummary(
    val quantity: Double = 0.0,
    val price: Int = 0,
    val orderType: OrderType,
)
