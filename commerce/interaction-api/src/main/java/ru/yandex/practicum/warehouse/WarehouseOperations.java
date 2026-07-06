package ru.yandex.practicum.warehouse;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseOperations {

    @PutMapping
    void addNewProduct(@RequestBody @Valid NewProductInWarehouseRequest request) throws FeignException;

    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto) throws FeignException;

    @PostMapping("/add")
    void addProduct(@RequestBody @Valid AddProductToWarehouseRequest request) throws FeignException;

    @GetMapping("/address")
    AddressDto getAddress() throws FeignException;

    @PostMapping("/shipped")
    void shipped(@RequestBody @Valid ShippedDeliveryRequest request) throws  FeignException;

    @PostMapping("/assembly")
    BookedProductsDto assembly(@RequestBody @Valid AssemblyProductsForOrderRequest request) throws  FeignException;

    @PostMapping("/return")
    void returnProducts(@RequestBody @NotNull Map<UUID, Integer> products) throws  FeignException;
}
