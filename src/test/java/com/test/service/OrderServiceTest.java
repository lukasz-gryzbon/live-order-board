package com.test.service;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.test.model.Order;
import com.test.repository.OrderRepository;
import com.test.service.helper.OrderSummaryService;
import com.test.shared.OrderBasedTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest extends OrderBasedTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderSummaryService orderSummaryService;

    @InjectMocks
    private OrderService underTest;

    @Test
    public void shouldRegisterOrder() {
        // GIVEN
        final Order order = randomOrder();

        // WHEN
        underTest.registerOrder(order);

        // THEN
        verify(orderRepository).save(order);
        verifyZeroInteractions(orderSummaryService);
    }

    @Test
    public void shouldCancelOrder() {
        // GIVEN
        final Order order = randomOrder();

        // WHEN
        underTest.cancelOrder(order);

        // THEN
        verify(orderRepository).remove(order);
        verifyZeroInteractions(orderSummaryService);
    }

    @Test
    public void shouldGetSummaryOfOrders() {
        // GIVEN
        final Order order = randomOrder();
        when(orderRepository.find()).thenReturn(asList(order));

        // WHEN
        underTest.getSummaryOfOrders();

        // THEN
        verify(orderSummaryService).summarise(asList(order));
        verify(orderRepository).find();
    }
}