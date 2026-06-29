package ru.yandex.practicum.warehouse.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.warehouse.WarehouseOperations;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WareHouseClient extends WarehouseOperations {

}
