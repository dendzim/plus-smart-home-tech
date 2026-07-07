package ru.yandex.practicum.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;


public interface OrderOperations {

    @GetMapping
    List<OrderDto> getOrders(@RequestParam @NotNull String username);

    @PutMapping
    OrderDto addOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto paymentOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto failPaymentOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto failDeliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId);
}
