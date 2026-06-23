package ru.yandex.practicum.controller;


import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.service.StorageService;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.feignClient.WareHouseClinet;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class StorageController implements WareHouseClinet {

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
}
