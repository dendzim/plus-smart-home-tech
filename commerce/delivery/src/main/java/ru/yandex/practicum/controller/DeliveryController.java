package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery.DeliveryOperations;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService service;

    @Override
    public DeliveryDto createDelivery(DeliveryDto request) throws FeignException {
        return service.createDelivery(request);
    }

    @Override
    public void deliverySuccessful(UUID deliveryId) throws FeignException {
        service.deliverySuccessful(deliveryId);
    }

    @Override
    public void deliveryPicked(UUID deliveryId) throws FeignException {
        service.deliveryPicked(deliveryId);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto request) throws FeignException {
        return service.deliveryCost(request);
    }

    @Override
    public void failedDelivery(UUID deliveryId) throws FeignException {
        service.failedDelivery(deliveryId);
    }
}
