package ru.yandex.practicum.order.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.order.OrderOperations;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderOperations {
}
