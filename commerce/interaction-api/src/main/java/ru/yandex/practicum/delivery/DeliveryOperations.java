package ru.yandex.practicum.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping
    DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto request);

    @PostMapping("/successful")
    void deliverySuccessful(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/picked")
    void deliveryPicked(@RequestBody @NotNull UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal deliveryCost(@RequestBody @Valid OrderDto request);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody @NotNull UUID deliveryId);
}
