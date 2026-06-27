package ru.yandex.practicum.warehouse;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

public interface WarehouseOperations {

    @PutMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void addProduct(@RequestBody @Valid AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getAddress();
}
