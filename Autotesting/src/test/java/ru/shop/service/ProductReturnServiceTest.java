package ru.shop.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shop.exception.EntityNotFoundException;
import ru.shop.model.*;
import ru.shop.repository.ProductReturnRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProductReturnServiceTest {
    private final ProductReturnRepository repository = Mockito.mock();
    private final ProductReturnService productReturnService = new ProductReturnService(repository);

    @Test
    public void shouldAddProductReturn() {
        //given
        var order = new Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 3, 6);
        var productReturn = new ProductReturn(UUID.randomUUID(), order.getId(), LocalDate.now(), order.getCount() - 1);

        //when
        productReturnService.add(order, productReturn.getQuantity());
        ArgumentCaptor<ProductReturn> orderArgumentCaptor = ArgumentCaptor.captor();

        verify(repository, times(1)).save(orderArgumentCaptor.capture());

        //then
        ProductReturn savedReturn = orderArgumentCaptor.getValue();

        assertEquals(productReturn.getQuantity(), savedReturn.getQuantity());
        assertEquals(order.getId(), savedReturn.getOrderId());
    }

    @Test
    public void shouldFindAllProductReturn() {
        //given
        List<ProductReturn> productReturns = List.of(new ProductReturn(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), (long) 10));

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
        ProductReturn productReturn = new ProductReturn(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), (long) 10);

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
    public void shouldThrowWhenProductNotFound() {
        // then
        assertThatThrownBy(() -> productReturnService.findById(UUID.randomUUID()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
