package ru.yandex.practicum.exception;

import java.util.UUID;

public class NoProductsInShoppingCartException extends RuntimeException {
    public NoProductsInShoppingCartException(String message) {
        super(message);
    }

    public NoProductsInShoppingCartException(UUID uuid) {
        super("Товара с id " + uuid + " нет в корзине");
    }
}
