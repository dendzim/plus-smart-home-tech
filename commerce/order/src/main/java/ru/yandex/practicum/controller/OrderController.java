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
        return service.addOrder(request);
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) throws FeignException {
        return service.returnOrder(request);
    }

    @Override
    public OrderDto paymentOrder(UUID orderId) throws FeignException {
        return service.paymentOrder(orderId);
    }

    @Override
    public OrderDto failPaymentOrder(UUID orderId) throws FeignException {
        return service.failPaymentOrder(orderId);
    }

    @Override
    public OrderDto deliveryOrder(UUID orderId) throws FeignException {
        return service.deliveryOrder(orderId);
    }

    @Override
    public OrderDto failDeliveryOrder(UUID orderId) throws FeignException {
        return service.failDeliveryOrder(orderId);
    }

    @Override
    public OrderDto completeOrder(UUID orderId) throws FeignException {
        return service.completeOrder(orderId);
    }

    @Override
    public OrderDto calculateTotalOrder(UUID orderId) throws FeignException {
        return service.calculateTotalOrder(orderId);
    }

    @Override
    public OrderDto calculateDeliveryOrder(UUID orderId) throws FeignException {
        return service.calculateDeliveryOrder(orderId);
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) throws FeignException {
        return service.assemblyOrder(orderId);
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) throws FeignException {
        return service.failAssemblyOrder(orderId);
    }
}
