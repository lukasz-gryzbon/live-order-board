package com.test.service.helper;

import static com.test.model.OrderType.SELL;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingInt;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class OrderSummaryService {

    private static final String MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE = "Missing argument: orders";
    private static final Comparator<OrderSummary> byPriceASC = comparingInt(OrderSummary::getPrice);

    // Alternatively, this method could return a list, but then the frontend would need to separate the SELL & BUY data
    public Map<OrderType, List<OrderSummary>> summarise(final List<Order> allOrders) {
        validate(allOrders);
        final Map<OrderType, List<Order>> ordersByOrderType = allOrders.stream().collect(groupingBy(Order::getOrderType));

        return Stream.of(OrderType.values()).collect(
                toMap(
                        identity(), orderType ->
                                ordersByOrderType.getOrDefault(orderType, emptyList()).stream().collect(
                                        collectingAndThen(
                                                groupingBy(Order::getPrice),
                                                ordersByPrice -> ordersByPrice.entrySet().stream().
                                                        map(priceAndOrdersEntry -> new OrderSummary(priceAndOrdersEntry.getValue().stream().mapToDouble(Order::getQuantity).sum(), priceAndOrdersEntry.getKey(), orderType)).
                                                        sorted(orderType == SELL ? byPriceASC : byPriceASC.reversed()).
                                                        collect(toList())
                                        ))));
    }

    private void validate(final List<Order> orders) {
        if (orders == null) {
            throw new IllegalArgumentException(MISSING_ARGUMENT_ORDERS_EXCEPTION_MESSAGE);
        }
    }
}