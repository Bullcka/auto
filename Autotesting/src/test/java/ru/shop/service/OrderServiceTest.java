package ru.shop.service;

import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shop.exception.BadOrderCountException;
import ru.shop.model.Customer;
import ru.shop.model.Order;
import ru.shop.model.Product;
import ru.shop.model.ProductType;
import ru.shop.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {
    private final OrderRepository repository = Mockito.mock();
    private final OrderService orderService = new OrderService(repository);

    @Test
    public void shouldAddOrder() {
        //given
        var customer = new Customer(UUID.randomUUID(), "name", "phone", 10);
        var product = new Product(UUID.randomUUID(), "name", 10, ProductType.GOOD);

        orderService.add(customer, product, 10);

        //when
        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.captor();

        verify(repository).save(orderArgumentCaptor.capture());

        //then
        Order savedOrder = orderArgumentCaptor.getValue();

        Assertions.assertEquals(10, savedOrder.getCount());
    }

    @Test
    public void ShouldThrowBadOrderCountException() {
        //given
        var customer = new Customer(UUID.randomUUID(), "name", "phone", 10);
        var product = new Product(UUID.randomUUID(), "name", 10, ProductType.GOOD);

        //then
        Assertions.assertThrows(
                BadOrderCountException.class,
                () -> orderService.add(customer, product, 0)
        );
    }

    @Test
    public void shouldFindByCustomerOrder() {
        //given
        var customer = new Customer(UUID.randomUUID(), "name", "phone", 10);
        var firstOrderId = UUID.randomUUID();
        var secondOrderId = UUID.randomUUID();

        when(repository.findAll()).thenReturn(
                List.of(
                        new Order(firstOrderId, customer.getId(),UUID.randomUUID(),10,10),
                        new Order(secondOrderId,customer.getId(),UUID.randomUUID(),10,10)
                )
        );

        //when
        List<Order> customerOrders = orderService.findByCustomer(customer);

        //then
        Assertions.assertEquals(2,customerOrders.size());
        assertThat(customerOrders)
                .hasSize(2)
                .extracting(Order::getId)
                .containsExactly(firstOrderId,secondOrderId);
    }

    @Test
    public void shouldGetTotalCustomerAmount() {
        //given
        var customer = new Customer(UUID.randomUUID(), "name", "phone", 10);
        var firstOrderId = UUID.randomUUID();
        var secondOrderId = UUID.randomUUID();

        when(repository.findAll()).thenReturn(
                List.of(
                        new Order(firstOrderId, customer.getId(),UUID.randomUUID(),10,10),
                        new Order(secondOrderId,customer.getId(),UUID.randomUUID(),10,10)
                )
        );

        //when
        long totalCustomerAmount = orderService.getTotalCustomerAmount(customer);

        //then
        Assertions.assertEquals(20,orderService.getTotalCustomerAmount(customer));
    }
}