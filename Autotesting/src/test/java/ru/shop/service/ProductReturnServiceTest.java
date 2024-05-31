package ru.shop.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shop.exception.EntityNotFoundException;
import ru.shop.model.*;
import ru.shop.repository.OrderRepository;
import ru.shop.repository.ProductReturnRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductReturnServiceTest {
    OrderService orderService = Mockito.mock(OrderService.class);
    private final ProductReturnRepository repository = Mockito.mock();
    private final ProductReturnService productReturnService = new ProductReturnService(repository, orderService);

    @Test
    void shouldAddProductReturn() {
        //given
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10, 1000);

        //when
        productReturnService.add(order, 10);

        //then
        ArgumentCaptor<ProductReturn> argumentCaptor = ArgumentCaptor.captor();
        ProductReturn savedProductReturn = argumentCaptor.getValue();

        verify(repository).save(argumentCaptor.capture());
        assertThat(savedProductReturn)
                .returns(10, ProductReturn::getQuantity)
                .returns(order.getId(), ProductReturn::getOrderId);
    }

    @Test
    public void shouldFindAllProductReturn() {
        //given
        List<ProductReturn> productReturns = List.of(new ProductReturn(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 3));

        when(repository.findAll()).thenReturn(productReturns);

        //when
        List<ProductReturn> result = productReturnService.findAll();

        //then
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetProductReturn() {
        //given
        UUID id = UUID.randomUUID();
        ProductReturn productReturn = new ProductReturn(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), 3);

        when(repository.findById(id)).thenReturn(Optional.of(productReturn));

        //when
        ProductReturn result = productReturnService.findById(id);

        //then
        assertEquals(productReturn, result);
    }

    @Test
    public void shouldFindNotExceptIdProductReturn() {
        //given
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> {productReturnService.findById(id);});
    }

    @Test
    public void shouldThrowWhenProductReturnNotFound() {
        //then
        assertThatThrownBy(() -> productReturnService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
} 