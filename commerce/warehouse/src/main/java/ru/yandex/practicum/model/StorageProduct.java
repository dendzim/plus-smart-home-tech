package ru.yandex.practicum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class StorageProduct {

    @Id
    private UUID productId;
    private Boolean fragile;
    private Double width;
    private Double height;
    private Double depth;
    private Double weight;
    private Long quantity;
}
