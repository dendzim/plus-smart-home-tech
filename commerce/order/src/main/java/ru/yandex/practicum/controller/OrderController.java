package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.OrderOperations;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService service;

    @Override
    public List<OrderDto> getOrders(String username) throws FeignException {
        return service.getOrders(username);
    }

    @Override
    public OrderDto addOrder(CreateNewOrderRequest request) throws FeignException {
        return service.adOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) throws FeignException {
        return null;
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto failPaymentOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto failDeliveryOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto completeOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) throws FeignException {
        return null;
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) throws FeignException {
        return null;
    }
}
