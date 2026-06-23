package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.mapper.StorageMapper;
import ru.yandex.practicum.repository.StorageRepository;
import ru.yandex.practicum.warehouse.dto.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@Service
@AllArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    public void addNewProduct(NewProductInWarehouseRequest request) {
    }

    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        return null;
    }

    public void addProduct(AddProductToWarehouseRequest request) {
    }

    public AddressDto getAddress() {
        return null;
    }
}
