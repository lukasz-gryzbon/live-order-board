package com.test.service.helper;

import static com.test.model.OrderType.BUY;
import static com.test.model.OrderType.SELL;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSummaryService {

    private static final String MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE = "Missing argument: orders";

    // Alternatively, this method could return a list, but then the frontend would need to separate the SELL & BUY data
    public Map<OrderType, List<OrderSummary>> summarise(final List<Order> orders) {
        validate(orders);
        final Map<OrderType, List<Order>> ordersByType = groupByOrderType(orders);
        return generateOrderSummary(ordersByType);
    }

    private Map<OrderType, List<Order>> groupByOrderType(final List<Order> orders) {
        final Map<OrderType, List<Order>> ordersByType = orders.stream().collect(groupingBy(Order::getOrderType));
        ordersByType.putIfAbsent(BUY, new ArrayList<>());
        ordersByType.putIfAbsent(SELL, new ArrayList<>());
        return ordersByType;
    }

    private Map<OrderType, List<OrderSummary>> generateOrderSummary(final Map<OrderType, List<Order>> ordersByType) {
        final Map<OrderType, List<OrderSummary>> orderSummary = new HashMap<>();
        orderSummary.put(BUY, generateOrderSummaries(ordersByType.get(BUY), BUY, comparingInt(OrderSummary::getPrice).reversed()));
        orderSummary.put(SELL, generateOrderSummaries(ordersByType.get(SELL), SELL, comparingInt(OrderSummary::getPrice)));
        return orderSummary;
    }

    private List<OrderSummary> generateOrderSummaries(final List<Order> orders, final OrderType orderType, final Comparator<OrderSummary> orderSummaryComparator) {
        final Map<Integer, List<Order>> ordersByPrice = orders.stream().collect(groupingBy(Order::getPrice));

        return ordersByPrice.keySet().stream()
                .map(price -> new OrderSummary(ordersByPrice.get(price).stream().mapToDouble(Order::getQuantity).sum(), price, orderType))
                .sorted(orderSummaryComparator)
                .collect(toList());
    }

    private void validate(final List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException(MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE);
        }
    }
}