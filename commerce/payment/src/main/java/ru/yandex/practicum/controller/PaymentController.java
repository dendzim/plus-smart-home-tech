package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.PaymentOperations;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService service;

    @Override
    public PaymentDto createPayment(OrderDto request) throws FeignException {
        return service.createPayment(request);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto request) throws FeignException {
        return service.getTotalCost(request);
    }

    @Override
    public BigDecimal getProductCost(OrderDto request) throws FeignException {
        return service.getProductCost(request);
    }

    @Override
    public void refundPayment(UUID paymentId) throws FeignException {
        service.refundPayment(paymentId);
    }

    @Override
    public void failedPayment(UUID paymentId) throws FeignException {
        service.failedPayment(paymentId);
    }
}
