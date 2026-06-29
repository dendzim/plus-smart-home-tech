package ru.yandex.practicum.store.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.store.StoreOperations;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface StoreClient extends StoreOperations {
}
