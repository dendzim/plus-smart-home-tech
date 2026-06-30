package ru.yandex.practicum.order;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.List;


public interface OrderOperations {

    @GetMapping
    List<OrderDto> getOrders(@RequestParam @NotNull String username) throws FeignException;

    @PutMapping
    OrderDto addOrder(@RequestBody CreateNewOrderRequest request) throws FeignException;
}
