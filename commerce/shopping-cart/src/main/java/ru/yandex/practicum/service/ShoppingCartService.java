package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;

    public ShoppingCartDto addProduct(String username, Map<UUID, Integer> products) {
        ShoppingCart shoppingCart = getOrCreate(username);

        Map<UUID, Integer> currentCart = shoppingCart.getProducts();

        products.forEach((id, i) -> currentCart.merge(id, i, Integer::sum));
        cartRepository.save(shoppingCart);

        return cartMapper.toShoppingCartDto(shoppingCart);
    }

    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = getOrCreate(username);;
        return cartMapper.toShoppingCartDto(shoppingCart);
    }

    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        return null;
    }

    public void deactivateCart(String username) {
        ShoppingCart shoppingCart = getOrCreate(username);
        shoppingCart.setActive(false);
        cartRepository.save(shoppingCart);
    }

    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> products) {
        ShoppingCart shoppingCart = getOrCreate(username);
        products.forEach(product -> shoppingCart.getProducts().remove(product));
        cartRepository.save(shoppingCart);
        return cartMapper.toShoppingCartDto(shoppingCart);
    }

    private ShoppingCart getOrCreate(String username) {
        ShoppingCart shoppingCart;
        if (cartRepository.findByUsernameAndActive(username, true) != null) {
            shoppingCart = cartRepository.findByUsernameAndActive(username, true);
        } else {
            shoppingCart = ShoppingCart.builder()
                    .username(username)
                    .active(true)
                    .products(new HashMap<>())
                    .build();
            cartRepository.save(shoppingCart);
        }

        if (shoppingCart.getProducts() == null) {
            shoppingCart.setProducts(new HashMap<>());
            cartRepository.save(shoppingCart);
        }
        return shoppingCart;
    }
}
