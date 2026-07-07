package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.exceptions.NoDeliveryFoundException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feignClient.OrderClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.dto.ShippedDeliveryRequest;
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
        Delivery delivery = deliveryMapper.toDelivery(request);
        delivery.setDeliveryState(DeliveryState.CREATED);
        deliveryRepository.save(delivery);

        return deliveryMapper.toDeliveryDto(delivery);
    }

    public void deliverySuccessful(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.deliveryOrder(delivery.getOrderId());
    }

    public void deliveryPicked(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        ShippedDeliveryRequest request = ShippedDeliveryRequest.builder()
                .deliveryId(deliveryId)
                .orderId(delivery.getOrderId())
                .build();

        wareHouseClient.shipped(request);
    }

    public BigDecimal deliveryCost(OrderDto request) {
        Delivery delivery = findDeliveryById(request.getDeliveryId());

        BigDecimal base = BigDecimal.valueOf(5);
        BigDecimal total = base;

        if (wareHouseClient.getAddress().getCity().contains("ADDRESS_1")) {
            base = base.multiply(BigDecimal.ONE);
        } else if (wareHouseClient.getAddress().getCity().contains("ADDRESS_2")) {
            base = base.multiply(BigDecimal.TWO);
        }
        total = total.add(base);

        if (request.isFragile()) {
            total = total.add(base.multiply(BigDecimal.valueOf(0.2)));
        }
        total = total.add(BigDecimal.valueOf(request.getDeliveryWeight()).multiply(BigDecimal.valueOf(0.3)));
        total = total.add(BigDecimal.valueOf(request.getDeliveryVolume()).multiply(BigDecimal.valueOf(0.2)));

        if (!wareHouseClient.getAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            return total.add(total.multiply(BigDecimal.valueOf(0.2)));
        }

        return total;
    }

    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        orderClient.failDeliveryOrder(delivery.getOrderId());
    }

    private Delivery findDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() -> new NoDeliveryFoundException(deliveryId));
    }
}
