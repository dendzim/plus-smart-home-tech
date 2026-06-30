package ru.yandex.practicum.order.feignClient;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
}
