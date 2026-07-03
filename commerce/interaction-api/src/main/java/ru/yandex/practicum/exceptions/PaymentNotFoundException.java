package ru.yandex.practicum.exceptions;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException(UUID paymentId) {
        super("Платед с id " + paymentId + " не найден");
    }
}
