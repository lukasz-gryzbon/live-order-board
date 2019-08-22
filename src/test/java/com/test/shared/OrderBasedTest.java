package com.test.shared;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;

import com.test.model.Order;
import com.test.model.OrderType;

public class OrderBasedTest {

    protected Order randomOrder() {
        return new Order(randomAlphanumeric(10), randomAlphanumeric(10), nextDouble(), nextInt(), randomOrderType());
    }

    protected OrderType randomOrderType() {
        return OrderType.values()[nextInt(0, 1)];
    }
}