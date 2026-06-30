package ru.yandex.practicum.cart;

import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartOperations {

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam @NotNull String username,
                               @RequestBody Map<UUID, Integer> products) throws FeignException;

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam @NotNull String username) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam @NotNull String username,
                                   @RequestBody ChangeProductQuantityRequest request) throws FeignException;

    @DeleteMapping
    void deactivateCart(@RequestParam @NotNull String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam @NotNull String username,
                                           @RequestBody List<UUID> productsId) throws FeignException;

}
