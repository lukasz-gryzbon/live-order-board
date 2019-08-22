package com.test.service.helper;

import static com.test.model.OrderType.BUY;
import static com.test.model.OrderType.SELL;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderSummaryServiceTest {

    private static final String EXPECTED_EXCEPTION_MESSAGE = "Missing argument: orders";

    private OrderSummaryService underTest;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        underTest = new OrderSummaryService();
    }

    @Test
    public void shouldSummariseSellOnlyOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, SELL));
        orders.add(new Order("2", "user2", 1.2, 310, SELL));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(BUY).size(), equalTo(0));
        assertThat(summary.get(SELL).size(), equalTo(2));
        assertOrderSummary(summary.get(SELL).get(0), 306, 3.5, SELL);
        assertOrderSummary(summary.get(SELL).get(1), 310, 1.2, SELL);
    }

    @Test
    public void shouldMergeSamePriceSellOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, SELL));
        orders.add(new Order("2", "user2", 1.2, 310, SELL));
        orders.add(new Order("3", "user3", 1.5, 307, SELL));
        orders.add(new Order("4", "user4", 2.0, 306, SELL));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(BUY).size(), equalTo(0));
        assertThat(summary.get(SELL).size(), equalTo(3));
        assertOrderSummary(summary.get(SELL).get(0), 306, 5.5, SELL);
        assertOrderSummary(summary.get(SELL).get(1), 307, 1.5, SELL);
        assertOrderSummary(summary.get(SELL).get(2), 310, 1.2, SELL);
    }

    @Test
    public void shouldSummariseBuyOnlyOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, BUY));
        orders.add(new Order("2", "user2", 1.2, 310, BUY));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(SELL).size(), equalTo(0));
        assertThat(summary.get(BUY).size(), equalTo(2));
        assertOrderSummary(summary.get(BUY).get(0), 310, 1.2, BUY);
        assertOrderSummary(summary.get(BUY).get(1), 306, 3.5, BUY);
    }

    @Test
    public void shouldMergeSamePriceBuyOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, BUY));
        orders.add(new Order("2", "user2", 1.2, 310, BUY));
        orders.add(new Order("3", "user3", 1.5, 307, BUY));
        orders.add(new Order("4", "user4", 2.0, 306, BUY));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(SELL).size(), equalTo(0));
        assertThat(summary.get(BUY).size(), equalTo(3));
        assertOrderSummary(summary.get(BUY).get(0), 310, 1.2, BUY);
        assertOrderSummary(summary.get(BUY).get(1), 307, 1.5, BUY);
        assertOrderSummary(summary.get(BUY).get(2), 306, 5.5, BUY);
    }

    @Test
    public void shouldSummariseBuyAndSellOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, BUY));
        orders.add(new Order("2", "user2", 1.2, 310, BUY));
        orders.add(new Order("3", "user3", 1.5, 307, BUY));
        orders.add(new Order("4", "user4", 2.0, 306, BUY));
        orders.add(new Order("1", "user1", 3.5, 306, SELL));
        orders.add(new Order("2", "user2", 1.2, 310, SELL));
        orders.add(new Order("3", "user3", 1.5, 307, SELL));
        orders.add(new Order("4", "user4", 2.0, 306, SELL));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(SELL).size(), equalTo(3));
        assertThat(summary.get(BUY).size(), equalTo(3));
        assertOrderSummary(summary.get(BUY).get(0), 310, 1.2, BUY);
        assertOrderSummary(summary.get(BUY).get(1), 307, 1.5, BUY);
        assertOrderSummary(summary.get(BUY).get(2), 306, 5.5, BUY);
        assertOrderSummary(summary.get(SELL).get(0), 306, 5.5, SELL);
        assertOrderSummary(summary.get(SELL).get(1), 307, 1.5, SELL);
        assertOrderSummary(summary.get(SELL).get(2), 310, 1.2, SELL);
    }

    @Test
    public void shouldSummariseMixedBuyAndSellOrders() {
        // GIVEN
        final List<Order> orders = new ArrayList<>();
        orders.add(new Order("1", "user1", 3.5, 306, BUY));
        orders.add(new Order("1", "user1", 3.5, 306, SELL));
        orders.add(new Order("2", "user2", 1.2, 310, BUY));
        orders.add(new Order("2", "user2", 1.2, 310, SELL));
        orders.add(new Order("3", "user3", 1.5, 307, BUY));
        orders.add(new Order("3", "user3", 1.5, 307, SELL));
        orders.add(new Order("4", "user4", 2.0, 306, BUY));
        orders.add(new Order("4", "user4", 2.0, 306, SELL));

        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(orders);

        // THEN
        assertThat(summary.get(SELL).size(), equalTo(3));
        assertThat(summary.get(BUY).size(), equalTo(3));
        assertOrderSummary(summary.get(BUY).get(0), 310, 1.2, BUY);
        assertOrderSummary(summary.get(BUY).get(1), 307, 1.5, BUY);
        assertOrderSummary(summary.get(BUY).get(2), 306, 5.5, BUY);
        assertOrderSummary(summary.get(SELL).get(0), 306, 5.5, SELL);
        assertOrderSummary(summary.get(SELL).get(1), 307, 1.5, SELL);
        assertOrderSummary(summary.get(SELL).get(2), 310, 1.2, SELL);
    }

    @Test
    public void shouldSummariseEmptyOrders() {
        // WEN
        final Map<OrderType, List<OrderSummary>> summary = underTest.summarise(emptyList());

        // THEN
        assertThat(summary.get(SELL).size(), equalTo(0));
        assertThat(summary.get(BUY).size(), equalTo(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectAttemptToSummariseAMissingListOfOrders() {
        underTest.summarise(null);
    }

    @Test
    public void shouldRejectAttemptToSummariseAMissingListOfOrdersWithMeaningfulMessage() {
        exception.expectMessage(EXPECTED_EXCEPTION_MESSAGE);
        exception.expect(IllegalArgumentException.class);

        underTest.summarise(null);
    }

    private void assertOrderSummary(final OrderSummary orderSummary, final int price, final double quantity, final OrderType orderType) {
        assertThat(orderSummary.getPrice(), equalTo(price));
        assertThat(orderSummary.getQuantity(), equalTo(quantity));
        assertThat(orderSummary.getOrderType(), equalTo(orderType));
    }
}