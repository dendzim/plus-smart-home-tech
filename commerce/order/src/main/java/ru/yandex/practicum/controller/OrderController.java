package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.OrderOperations;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService service;

    @Override
    public List<OrderDto> getOrders(String username) throws FeignException {
        return service.getOrders(username);
    }
}
