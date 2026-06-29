package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedProductsDto {
    @NotNull
    private double deliveryWeight;
    @NotNull
    private double deliveryVolume;
    @NotNull
    private boolean fragile;
}