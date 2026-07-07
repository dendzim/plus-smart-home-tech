package ru.yandex.practicum.controller;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.service.StorageService;
import ru.yandex.practicum.warehouse.WarehouseOperations;
import ru.yandex.practicum.warehouse.dto.*;
import ru.yandex.practicum.warehouse.feignClient.WareHouseClient;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class StorageController implements WarehouseOperations {

    private final StorageService service;

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) throws FeignException {
        service.addNewProduct(request);
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) throws FeignException {
        return service.checkQuantity(shoppingCartDto);
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) throws FeignException {
        service.addProduct(request);
    }

    @Override
    public AddressDto getAddress() {
        return service.getAddress();
    }

    @Override
    public void shipped(ShippedDeliveryRequest request) throws FeignException {
        service.shippedToDelivery(request);
    }

    @Override
    public BookedProductsDto assembly(AssemblyProductsForOrderRequest request) throws FeignException {
        return service.assembly(request);
    }

    @Override
    public void returnProducts(Map<UUID, Integer> products) throws FeignException {
        service.returnProducts(products);
    }
}
