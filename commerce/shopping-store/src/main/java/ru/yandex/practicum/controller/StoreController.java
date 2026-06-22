package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.ProductService;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.QuantityState;
import ru.yandex.practicum.store.feignClient.StoreClient;

import java.awt.print.Pageable;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class StoreController implements StoreClient {

    private final ProductService service;

    @Override
    public ProductDto addProduct(ProductDto productDto) throws FeignException {
        return service.addProduct(productDto);
    }

    @Override
    public ProductDto getProduct(UUID productId) throws FeignException {
        return service.getProduct(productId);
    }

    @Override
    public Page<ProductDto> getProductsByProductCategory(ProductCategory productCategory,
                                                         Pageable pageable) throws FeignException {
        return service.getProductsByProductCategory(productCategory, pageable);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) throws FeignException {
        return service.updateProduct(productDto);
    }

    @Override
    public Boolean setQuantityState(UUID productId, QuantityState quantityState) throws FeignException {
        return service.setQuantityState(productId, quantityState);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) throws FeignException {
        return service.removeProductFromStore(productId);
    }
}
