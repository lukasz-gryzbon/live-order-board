package com.test.repository;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomUtils.nextDouble;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.test.model.Order;
import com.test.model.OrderType;
import com.test.shared.OrderBasedTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class OrderRepositoryTest extends OrderBasedTest {

    private static final String EXPECTED_EXCEPTION_MESSAGE = "Order with the given id already stored";

    private final OrderRepository underTest = new InMemoryOrderRepository();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldBeEmptyInitially() {
        // WHEN
        final List<Order> orders = underTest.find();

        // THEN
        assertThat(orders, is(empty()));
    }

    @Test
    public void shouldSaveOne() {
        // GIVEN
        final String userId = randomAlphanumeric(10);
        final double quantity = nextDouble();
        final int price = nextInt();
        final OrderType orderType = randomOrderType();
        final String orderId = "orderId";

        final Order order = new Order(orderId, userId, quantity, price, orderType);

        // WHEN
        underTest.save(order);

        // THEN
        final List<Order> storedOrders = underTest.find();
        assertThat(storedOrders, hasSize(1));
        final Order storedOrder = storedOrders.get(0);
        assertThat(storedOrder.getId(), equalTo(orderId));
        assertThat(storedOrder.getUserId(), equalTo(userId));
        assertThat(storedOrder.getQuantity(), equalTo(quantity));
        assertThat(storedOrder.getPrice(), equalTo(price));
        assertThat(storedOrder.getOrderType(), equalTo(orderType));
    }

    @Test
    public void shouldSaveMultiple() {
        // GIVEN
        final Order order1 = randomOrder();
        final Order order2 = randomOrder();
        final Order order3 = randomOrder();

        // WHEN
        underTest.save(order1);
        underTest.save(order2);
        underTest.save(order3);

        // THEN
        final List<Order> storedOrders = underTest.find();
        assertThat(storedOrders, hasSize(3));
        assertThat(storedOrders, containsInAnyOrder(order1, order2, order3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotOverwriteExistingOrderWithSameId() {
        // GIVEN
        final Order order1 = new Order("orderId", randomAlphanumeric(10), nextDouble(), nextInt(), randomOrderType());
        final Order order2 = new Order("orderId", randomAlphanumeric(10), nextDouble(), nextInt(), randomOrderType());
        underTest.save(order1);

        // WHEN
        underTest.save(order2);
    }

    @Test
    public void shouldThrowExceptionWithMeaningfulMessageWhenAttemptingToOverwriteExistingOrderWithSameId() {
        // GIVEN
        exception.expectMessage(EXPECTED_EXCEPTION_MESSAGE);
        exception.expect(IllegalArgumentException.class);
        final Order order1 = new Order("orderId", randomAlphanumeric(10), nextDouble(), nextInt(), randomOrderType());
        final Order order2 = new Order("orderId", randomAlphanumeric(10), nextDouble(), nextInt(), randomOrderType());
        underTest.save(order1);

        // WHEN
        underTest.save(order2);
    }

    @Test
    public void shouldRemove() {
        // GIVEN
        final Order order = randomOrder();
        underTest.save(order);

        // WHEN
        underTest.remove(order);

        // THEN
        assertThat(underTest.find(), is(empty()));
    }

    @Test
    public void shouldFindAll() {
        // GIVEN
        final int numberOfOrders = nextInt(10, 50);

        // WHEN
        for (int i = 0; i < numberOfOrders; i++) {
            underTest.save(randomOrder());
        }

        // THEN
        assertThat(underTest.find(), hasSize(numberOfOrders));
    }
}