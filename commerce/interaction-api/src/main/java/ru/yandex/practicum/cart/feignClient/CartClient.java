package ru.yandex.practicum.cart.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.cart.CartOperations;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient extends CartOperations {

}
