package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.model.ShoppingCart;

@Component
public class ShoppingCartMapper {

    public ShoppingCart toShoppingCart(ShoppingCartDto shoppingCartDto) {
        if (shoppingCartDto == null) {
            return null;
        }

        return ShoppingCart.builder().build();
    }

    public ShoppingCartDto toShoppingCartDto(ShoppingCart shoppingCart) {
        if (shoppingCart == null) {
            return null;
        }

        return ShoppingCartDto.builder().build();
    }
}
