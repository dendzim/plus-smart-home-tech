package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.feignClient.CartClient;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements CartClient {

    private final ShoppingCartService service;

    @Override
    public ShoppingCartDto addProduct(String username, Map<UUID, Integer> products) {
        return service.addProduct(username, products);
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return service.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        return service.changeQuantity(username, request);
    }

    @Override
    public void deactivateCart(String username) {
        service.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        return service.removeProductsFromCart(username, products);
    }
}
