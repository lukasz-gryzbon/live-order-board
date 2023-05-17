package com.test.service

import com.test.repository.OrderRepository
import com.test.service.helper.OrderSummaryService
import com.test.shared.OrderBasedTest
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class OrderServiceTest : OrderBasedTest() {
    private val orderRepository = mockk<OrderRepository>(relaxed = true)
    private val orderSummaryService = mockk<OrderSummaryService>(relaxed = true)
    private val underTest = OrderService(orderRepository, orderSummaryService)

    @Test
    fun shouldRegisterOrder() {
        // GIVEN
        val order = randomOrder()

        // WHEN
        underTest.registerOrder(order)

        // THEN
        verify {orderRepository.save(order) }
        verify {orderSummaryService wasNot Called }
    }

    @Test
    fun shouldCancelOrder() {
        // GIVEN
        val order = randomOrder()

        // WHEN
        underTest.cancelOrder(order)

        // THEN
        verify {orderRepository.remove(order) }
        verify {orderSummaryService wasNot Called }
    }

    @Test
    fun shouldGetSummaryOfOrders() {
        // GIVEN
        val order = randomOrder()
        every {  orderRepository.find() } returns listOf(order)

        // WHEN
        underTest.summaryOfOrders

        // THEN
        verify { orderSummaryService.summarise(listOf(order)) }
        verify {orderRepository.find() }
    }
}
