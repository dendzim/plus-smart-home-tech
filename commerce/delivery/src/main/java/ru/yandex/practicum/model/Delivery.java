package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.warehouse.dto.AddressDto;

import java.util.UUID;

@Entity
@Table(name = "deliveries", schema = "delivery")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryId;

    private AddressDto fromAddress;

    private AddressDto toAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryState deliveryState;
}
