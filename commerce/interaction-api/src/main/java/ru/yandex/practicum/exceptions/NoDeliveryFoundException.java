package ru.yandex.practicum.exceptions;

import java.util.UUID;

public class NoDeliveryFoundException extends RuntimeException {
    public NoDeliveryFoundException(String message) {
        super(message);
    }

    public NoDeliveryFoundException(UUID productId) {
        super("Доставка с id " + productId + " не найдена");
    }
}
