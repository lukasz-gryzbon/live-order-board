package com.test.repository;

import com.test.model.Order;
import com.test.repository.exception.OrderAlreadyStoredException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private static final String EXCEPTION_MESSAGE = "Order with the given id already stored";

    private final Map<String, Order> orders = new HashMap<>();

    // An alternative could be to overwrite the existing order
    @Override
    public void save(final Order order) {
        validate(order);
        orders.put(order.getId(), order);
    }

    @Override
    public void remove(final Order order) {
        orders.remove(order.getId());
    }

    @Override
    public List<Order> find() {
        return new ArrayList<>(orders.values());
    }

    private void validate(final Order order) {
        if (orders.containsKey(order.getId())) {
            throw new OrderAlreadyStoredException(EXCEPTION_MESSAGE);
        }
    }
}
