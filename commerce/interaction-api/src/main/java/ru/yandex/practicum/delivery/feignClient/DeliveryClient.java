package ru.yandex.practicum.delivery.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.delivery.DeliveryOperations;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient extends DeliveryOperations {
}
