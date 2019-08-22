package com.test.service;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;
import com.test.repository.OrderRepository;
import com.test.service.helper.OrderSummaryService;

import java.util.List;
import java.util.Map;

public class OrderService {

    private final OrderRepository memoryOrderRepository;

    private final OrderSummaryService orderSummaryService;

    public OrderService(final OrderRepository memoryOrderRepository, final OrderSummaryService orderSummaryService) {
        this.memoryOrderRepository = memoryOrderRepository;
        this.orderSummaryService = orderSummaryService;
    }

    public Order registerOrder(final Order order) {
        memoryOrderRepository.save(order);
        return order;
    }

    public Order cancelOrder(final Order order) {
        memoryOrderRepository.remove(order);
        return order;
    }

    public Map<OrderType, List<OrderSummary>> getSummaryOfOrders() {
        return orderSummaryService.summarise(memoryOrderRepository.find());
    }
}
