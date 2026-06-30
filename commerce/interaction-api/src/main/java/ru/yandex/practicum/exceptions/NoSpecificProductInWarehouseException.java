package ru.yandex.practicum.exceptions;


import java.util.UUID;

public class NoSpecificProductInWarehouseException extends RuntimeException {
    public NoSpecificProductInWarehouseException(String message) {
        super(message);
    }

    public NoSpecificProductInWarehouseException(UUID productId) {
        super("Товар с id " + productId + " не найден на складе");
    }
}
