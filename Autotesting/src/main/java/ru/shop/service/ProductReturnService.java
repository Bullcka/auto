package ru.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.shop.exception.BadProductReturnCountException;
import ru.shop.exception.EntityNotFoundException;
import ru.shop.model.Order;
import ru.shop.model.ProductReturn;
import ru.shop.repository.ProductReturnRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductReturnService {
    private final ProductReturnRepository repository;
    private final OrderService orderService;

    public void add(Order order, int count) {
        Order initialOrder = orderService.getById(order.getId());
        if (initialOrder.getCount() < count) {
            throw new BadProductReturnCountException();
        }
        ProductReturn productReturn = new ProductReturn(UUID.randomUUID(),order.getId(), LocalDate.now(),count);
        repository.save(productReturn);
    }

    public List<ProductReturn> findAll() {
        return repository.findAll();
    }

    public ProductReturn findById(UUID id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
