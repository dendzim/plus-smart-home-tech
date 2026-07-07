package ru.yandex.practicum.payment.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.payment.PaymentOperations;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentOperations {
}
