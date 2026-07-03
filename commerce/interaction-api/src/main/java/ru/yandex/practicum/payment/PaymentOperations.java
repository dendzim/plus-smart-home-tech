package ru.yandex.practicum.payment;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping
    PaymentDto createPayment(@RequestBody @Valid OrderDto request) throws FeignException;

    @PostMapping("/totalCost")
    BigDecimal totalCost(@RequestBody @Valid OrderDto request) throws FeignException;

    @PostMapping("/productCost")
    BigDecimal productCost(@RequestBody @Valid OrderDto request) throws FeignException;

    @PostMapping("/refund")
    void refundPayment(@RequestBody @NotNull UUID paymentId) throws FeignException;

    @PostMapping("/failed")
    void failedPayment(@RequestBody @NotNull UUID paymentId) throws FeignException;
}
