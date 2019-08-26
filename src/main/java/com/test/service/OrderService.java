package com.test.service;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;
import com.test.repository.OrderRepository;
import com.test.service.helper.OrderSummaryService;

import java.util.List;
import java.util.Map;

public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderSummaryService orderSummaryService;

    public OrderService(final OrderRepository orderRepository, final OrderSummaryService orderSummaryService) {
        this.orderRepository = orderRepository;
        this.orderSummaryService = orderSummaryService;
    }

    public Order registerOrder(final Order order) {
        orderRepository.save(order);
        return order;
    }

    public Order cancelOrder(final Order order) {
        orderRepository.remove(order);
        return order;
    }

    public Map<OrderType, List<OrderSummary>> getSummaryOfOrders() {
        return orderSummaryService.summarise(orderRepository.find());
    }
}
