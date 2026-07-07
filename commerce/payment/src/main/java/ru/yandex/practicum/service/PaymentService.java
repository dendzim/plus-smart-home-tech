package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exceptions.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.exceptions.PaymentNotFoundException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.feignClient.OrderClient;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.enums.PaymentState;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.store.feignClient.StoreClient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
    private final BigDecimal FEE_COEFFICIENT = BigDecimal.valueOf(0.1);

    private final PaymentRepository paymentRepository;
    private final StoreClient storeClient;
    private final OrderClient orderClient;
    private final PaymentMapper paymentMapper;

    public PaymentDto createPayment(OrderDto request) {
        Payment payment = paymentMapper.toPayment(request);
        payment.setPaymentState(PaymentState.PENDING);

        paymentRepository.save(payment);

        return paymentMapper.toPaymentDto(payment);
    }

    public BigDecimal getTotalCost(OrderDto request) {
        BigDecimal products = request.getProductPrice();
        BigDecimal delivery = request.getDeliveryPrice();
        if (products == null || delivery == null) {
            throw new NotEnoughInfoInOrderToCalculateException();
        }
        products = products.add(products.multiply(FEE_COEFFICIENT));

        return products.add(delivery);
    }

    public BigDecimal getProductCost(OrderDto request) {
        BigDecimal totalProductsCost = BigDecimal.ZERO;

        for (Map.Entry<UUID, Integer> entry : request.getProducts().entrySet()) {
            BigDecimal price = storeClient.getProduct(entry.getKey()).getPrice();
            BigDecimal total = price.multiply(BigDecimal.valueOf(entry.getValue()));
            totalProductsCost = totalProductsCost.add(total);
        }

        return totalProductsCost;
    }

    public void refundPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        paymentRepository.save(payment);

        orderClient.paymentOrder(paymentId);
    }

    public void failedPayment(UUID paymentId) {
        Payment payment = findPaymentById(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        paymentRepository.save(payment);

        orderClient.failPaymentOrder(paymentId);
    }

    private Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}
