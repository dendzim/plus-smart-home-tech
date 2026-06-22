package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.awt.print.Pageable;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDto addProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductDto getProduct(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.toProductDto(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getProductsByProductCategory(ProductCategory category, Pageable pageable) {
        Page<Product> products = productRepository.findByProductCategory(category, pageable);
        return products.map(productMapper::toProductDto);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID productId = productDto.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        productMapper.update(product, productDto);

        return productMapper.toProductDto(product);
    }

    public Boolean setQuantityState(UUID productId, QuantityState quantityState) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setQuantityState(quantityState);
        productRepository.save(product);
        return true;
    }

    public Boolean removeProductFromStore(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setProductState(ProductState.DEACTIVATE);
        productRepository.save(product);
        return true;
    }
}
