package ru.yandex.practicum.cart;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartOperations {

    @PutMapping
    ShoppingCartDto addProduct(@RequestParam @NotNull String username, @RequestBody Map<UUID, Integer> products);

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam @NotNull String username);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantity(@RequestParam @NotNull String username,
                                   @RequestBody ChangeProductQuantityRequest request);

    @DeleteMapping
    void deactivateCart(@RequestParam @NotNull String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam @NotNull String username,
                                           @RequestBody List<UUID> productsId);

}
