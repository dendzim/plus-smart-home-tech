package ru.yandex.practicum.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private QuantityState quantityState;

    @NotNull
    private ProductState productState;

    private ProductCategory productCategory;

    @Min(value = 1)
    @NotNull
    private BigDecimal price;
}
