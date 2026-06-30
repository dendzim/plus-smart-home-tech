package ru.yandex.practicum.delivery.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {
}
