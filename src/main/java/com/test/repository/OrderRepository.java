package com.test.repository;

import com.test.model.Order;

import java.util.List;

public interface OrderRepository {
    void save(final Order order);

    void remove(Order order);

    List<Order> find();
}
