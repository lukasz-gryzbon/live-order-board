package com.test.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Order {

    private final String id;
    private final String userId;
    private final double quantity;
    private final int price;
    private final OrderType orderType;
}
