package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.StorageProduct;

import java.util.UUID;

public interface StorageRepository extends JpaRepository<StorageProduct, UUID> {
}
