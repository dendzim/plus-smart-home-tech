package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.dto.ProductDto;

@Component
public class ProductMapper {

    public ProductDto toProductDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productCategory(product.getProductCategory())
                .productState(product.getProductState())
                .price(product.getPrice())
                .build();
    }

    public Product toProduct(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        return Product.builder()
                .productId(productDto.getProductId())
                .productName(productDto.getProductName())
                .description(productDto.getDescription())
                .imageSrc(productDto.getImageSrc())
                .quantityState(productDto.getQuantityState())
                .productCategory(productDto.getProductCategory())
                .productState(productDto.getProductState())
                .price(productDto.getPrice())
                .build();
    }

    public void update(Product product, ProductDto productDto) {
        if (productDto == null) {
            return;
        }

        if (productDto.getProductName() != null) {
            product.setProductName(productDto.getProductName());
        }
        if (productDto.getDescription() != null) {
            product.setDescription(productDto.getDescription());
        }
        if (productDto.getImageSrc() != null) {
            product.setImageSrc(productDto.getImageSrc());
        }
        if (productDto.getQuantityState() != null) {
            product.setQuantityState(productDto.getQuantityState());
        }
        if (productDto.getProductCategory() != null) {
            product.setProductCategory(productDto.getProductCategory());
        }
        if (productDto.getProductState() != null) {
            product.setProductState(productDto.getProductState());
        }
        if (productDto.getPrice() != null) {
            product.setPrice(productDto.getPrice());
        }
    }
}
