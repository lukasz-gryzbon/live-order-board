package com.test.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderSummary {

    private final double quantity;
    private final int price;
    private final OrderType orderType;
}
