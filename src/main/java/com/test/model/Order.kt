package com.test.model

data class Order(
    val id: String,
    val userId: String,
    val quantity: Double = 0.0,
    val price: Int = 0,
    val orderType: OrderType,
)
