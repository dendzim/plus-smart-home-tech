package ru.yandex.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.StorageProduct;
import ru.yandex.practicum.warehouse.dto.NewProductInWarehouseRequest;

@Component
public class StorageMapper {

    private final long QUANTITY = 0L;

    public StorageProduct toStorageProduct(NewProductInWarehouseRequest request) {
        if (request == null) {
            return null;
        }

        StorageProduct.StorageProductBuilder builder = StorageProduct.builder()
                .productId(request.getProductId())
                .fragile(request.getFragile())
                .weight(request.getWeight())
                .quantity(QUANTITY);

        if (request.getDimension() != null) {
            builder.width(request.getDimension().getWidth())
                    .height(request.getDimension().getHeight())
                    .depth(request.getDimension().getDepth());
        }

        return builder.build();
    }
}
