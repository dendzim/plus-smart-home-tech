package ru.yandex.practicum.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import ru.yandex.practicum.exceptions.NoSpecificProductInWarehouseException;
import ru.yandex.practicum.exceptions.ValidationException;
import ru.yandex.practicum.mapper.StorageMapper;
import ru.yandex.practicum.model.OrderBooking;
import ru.yandex.practicum.model.StorageProduct;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.StorageRepository;
import ru.yandex.practicum.warehouse.dto.*;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;
    private final BookingRepository bookingRepository;

    private static final String[] ADDRESSES =
            new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    public void addNewProduct(NewProductInWarehouseRequest request) {
        if (storageRepository.existsById(request.getProductId())) {
            throw new ValidationException("Такой продукт уже есть " + request.getProductId());
        }

        StorageProduct product = storageMapper.toStorageProduct(request);
        storageRepository.save(product);
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        if (shoppingCartDto == null) {
            throw new ValidationException("Корзина пустая");
        }

        BookedProductsDto bookedProducts = new BookedProductsDto();

        Map<UUID, StorageProduct> productsById =
                storageRepository.findAllById(shoppingCartDto.getProducts().keySet()).stream()
                        .collect(Collectors.toMap(StorageProduct::getProductId, Function.identity()));

        for (Map.Entry<UUID, Integer> entry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = entry.getKey();
            Integer quantity = entry.getValue();

            StorageProduct storageProduct = productsById.get(productId);

            if (storageProduct == null) {
                throw new NoSpecificProductInWarehouseException(productId);
            }

            if (storageProduct.getQuantity() < quantity) {
                throw new ValidationException("Недостаточно продуктов с id: " + productId);
            }

            bookedProducts.setFragile(bookedProducts.isFragile() || storageProduct.getFragile());
            bookedProducts.setDeliveryWeight(bookedProducts.getDeliveryWeight() + storageProduct.getWeight() *
                    quantity);

            double volume = storageProduct.getWidth() * storageProduct.getDepth() * storageProduct.getHeight();
            bookedProducts.setDeliveryVolume(bookedProducts.getDeliveryVolume() + volume * quantity);
        }

        return bookedProducts;
    }

    public void addProduct(AddProductToWarehouseRequest request) {
        StorageProduct storageProduct = storageRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecificProductInWarehouseException(request.getProductId()));

        storageProduct.setQuantity(storageProduct.getQuantity() + request.getQuantity());
        storageRepository.save(storageProduct);
    }

    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    public void shippedToDelivery(ShippedDeliveryRequest request) {
        OrderBooking booking = bookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("No booking found for order " + request.getOrderId()));

        booking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(booking);
    }
}
