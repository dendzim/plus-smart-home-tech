package ru.yandex.practicum.exceptions;

import java.util.UUID;

public class NoOrderFoundException extends RuntimeException {
    public NoOrderFoundException(String message) {
        super(message);
    }

    public NoOrderFoundException(UUID orderId) {
        super("Заказ с id " + orderId + " не найден");
    }
}
