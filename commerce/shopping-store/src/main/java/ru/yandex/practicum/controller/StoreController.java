package ru.yandex.practicum.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.ProductService;

import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.QuantityState;
import ru.yandex.practicum.store.feignClient.StoreClient;

import org.springframework.data.domain.Pageable;

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
    public Page<ProductDto> getProductsByCategory(ProductCategory category, int page, int size, String sort) {
        Sort sortObj = parseSortParams(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        return service.getProductsByProductCategory(category, pageable);
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

    private Sort parseSortParams(String sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        String[] parts = sortParams.split(",");
        String property = parts[0].trim();
        Sort sort = Sort.unsorted();
        Sort.Direction direction = parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        sort = sort.and(Sort.by(direction, property));
        return sort;
    }
}
