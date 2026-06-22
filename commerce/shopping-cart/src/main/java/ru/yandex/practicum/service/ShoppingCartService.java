package ru.yandex.practicum.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShoppingCartService {
    public ShoppingCartDto addProduct(String username, Map<UUID, Integer> products) {
        return null;
    }

    public ShoppingCartDto getShoppingCart(String username) {
        return null;
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        return null;
    }

    public void deactivateCart(String username) {
    }

    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productsId) {
        return null;
    }
}
