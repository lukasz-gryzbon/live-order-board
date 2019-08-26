package com.test.acceptance;

import static com.test.model.OrderType.BUY;
import static com.test.model.OrderType.SELL;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import com.test.model.Order;
import com.test.model.OrderSummary;
import com.test.model.OrderType;
import com.test.repository.InMemoryOrderRepository;
import com.test.service.OrderService;
import com.test.service.helper.OrderSummaryService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class LiveOrderBoardTest {

    private OrderService orderService;

    @Before
    public void setup() {
        orderService = new OrderService(new InMemoryOrderRepository(), new OrderSummaryService());
    }

    @Test
    public void shouldGenerateEmptyOrderData() {
        // WHEN
        final Map<OrderType, List<OrderSummary>> summaryOfOrders = orderService.getSummaryOfOrders();

        // THEN
        assertThat(summaryOfOrders.get(BUY), hasSize(0));
        assertThat(summaryOfOrders.get(SELL), hasSize(0));
    }

    @Test
    public void shouldMergeSamePriceOrders() {
        // GIVEN
        orderService.registerOrder(newOrder("order1", "user1", 3.5, 306, SELL));
        orderService.registerOrder(newOrder("order2", "user2", 1.2, 306, SELL));

        // WHEN
        final Map<OrderType, List<OrderSummary>> summaryOfOrders = orderService.getSummaryOfOrders();

        // THEN
        assertThat(summaryOfOrders.get(BUY), hasSize(0));
        final List<OrderSummary> orderSummaries = summaryOfOrders.get(SELL);
        assertThat(orderSummaries, hasSize(1));
        assertOrderSummary(orderSummaries.get(0), 4.7, 306);
    }

    @Test
    public void shouldGenerateSortedSellOrderData() {
        // GIVEN
        orderService.registerOrder(newOrder("order1", "user1", 3.5, 306, SELL));
        orderService.registerOrder(newOrder("order2", "user2", 1.2, 310, SELL));
        orderService.registerOrder(newOrder("order3", "user3", 1.5, 307, SELL));
        orderService.registerOrder(newOrder("order4", "user4", 2.0, 306, SELL));

        // WHEN
        final Map<OrderType, List<OrderSummary>> summaryOfOrders = orderService.getSummaryOfOrders();

        // THEN
        assertThat(summaryOfOrders.get(BUY), hasSize(0));
        final List<OrderSummary> orderSummaries = summaryOfOrders.get(SELL);
        assertThat(orderSummaries, hasSize(3));
        assertOrderSummary(orderSummaries.get(0), 5.5, 306);
        assertOrderSummary(orderSummaries.get(1), 1.5, 307);
        assertOrderSummary(orderSummaries.get(2), 1.2, 310);
    }

    @Test
    public void shouldGenerateSortedBuyOrderData() {
        // GIVEN
        orderService.registerOrder(newOrder("order1", "user1", 3.5, 306, BUY));
        orderService.registerOrder(newOrder("order2", "user2", 1.2, 310, BUY));
        orderService.registerOrder(newOrder("order3", "user3", 1.5, 307, BUY));
        orderService.registerOrder(newOrder("order4", "user4", 2.0, 306, BUY));

        // WHEN
        final Map<OrderType, List<OrderSummary>> summaryOfOrders = orderService.getSummaryOfOrders();

        // THEN
        assertThat(summaryOfOrders.get(SELL), hasSize(0));
        final List<OrderSummary> orderSummaries = summaryOfOrders.get(BUY);
        assertThat(orderSummaries, hasSize(3));
        assertOrderSummary(orderSummaries.get(0), 1.2, 310);
        assertOrderSummary(orderSummaries.get(1), 1.5, 307);
        assertOrderSummary(orderSummaries.get(2), 5.5, 306);
    }

    @Test
    public void shouldGenerateSortedBuyAndSellOrderDataAfterRemovalOperations() {
        // GIVEN
        final Order order1 = newOrder("order1", "user1", 3.5, 306, BUY);
        final Order order2 = newOrder("order2", "user2", 1.2, 310, BUY);
        final Order order3 = newOrder("order3", "user3", 1.5, 307, SELL);

        orderService.registerOrder(order1);
        orderService.registerOrder(order2);
        orderService.cancelOrder(order1);
        orderService.registerOrder(order3);

        // WHEN
        final Map<OrderType, List<OrderSummary>> summaryOfOrders = orderService.getSummaryOfOrders();

        // THEN
        final List<OrderSummary> sellOrderSummaries = summaryOfOrders.get(SELL);
        assertThat(sellOrderSummaries, hasSize(1));
        assertOrderSummary(sellOrderSummaries.get(0), 1.5, 307);

        final List<OrderSummary> orderSummaries = summaryOfOrders.get(BUY);
        assertThat(orderSummaries, hasSize(1));
        assertOrderSummary(orderSummaries.get(0), 1.2, 310);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToRegisterOrderWithAnExistingId() {
        // GIVEN
        orderService.registerOrder(newOrder("order1", "user1", 3.5, 306, BUY));

        // WHEN
        orderService.registerOrder(newOrder("order1", "user2", 1.2, 310, BUY));
    }

    private void assertOrderSummary(final OrderSummary orderSummary, final double quantity, final int price) {
        assertThat(orderSummary.getQuantity(), equalTo(quantity));
        assertThat(orderSummary.getPrice(), equalTo(price));
    }

    private Order newOrder(final String id, final String userId, final double quantity, final int price, final OrderType orderType) {
        return new Order(id, userId, quantity, price, orderType);
    }
}
