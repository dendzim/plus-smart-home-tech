package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {
    public PaymentDto createPayment(OrderDto request) {
        return null;
    }

    public BigDecimal totalCost(OrderDto request) {
        return null;
    }

    public BigDecimal productCost(OrderDto request) {
        return null;
    }

    public void refundPayment(UUID paymentId) {
    }

    public void failedPayment(UUID paymentId) {
    }
}
