package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feignClient.OrderClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.feignClient.WareHouseClient;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WareHouseClient wareHouseClient;

    public DeliveryDto createDelivery(DeliveryDto request) {
        return null;
    }

    public void deliverySuccessful(UUID deliveryId) {
    }

    public void deliveryPicked(UUID deliveryId) {
    }

    public BigDecimal deliveryCost(OrderDto request) {
        return null;
    }

    public void failedDelivery(UUID deliveryId) {
    }
}
