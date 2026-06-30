package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.delivery.feignClient.DeliveryClient;
import ru.yandex.practicum.exceptions.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.CreateNewOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.dto.ProductReturnRequest;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.payment.feignClient.PaymentClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.feignClient.WareHouseClient;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WareHouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Transactional(readOnly = true)
    public List<OrderDto> getOrders(String username) {
        return orderRepository.findAllByUsername(username).stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    public OrderDto addOrder(CreateNewOrderRequest request) {
        ShoppingCartDto cart = request.getShoppingCart();

        BookedProductsDto booked = warehouseClient.checkQuantity(cart);
        Order order = createOrder(request, booked);
        orderRepository.save(order);

        return orderMapper.toOrderDto(order);
    }

    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = findOrderById(request.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto paymentOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.PAID);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto failPaymentOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto deliveryOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto failDeliveryOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto completeOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto calculateTotalOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        return null;
    }

    public OrderDto calculateDeliveryOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        return null;
    }

    public OrderDto assemblyOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    public OrderDto failAssemblyOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NoOrderFoundException(orderId));
    }

    private Order createOrder(CreateNewOrderRequest request, BookedProductsDto booked) {
        Order order = new Order();
        order.setShopingCartId(request.getShoppingCart().getShoppingCartId());
        order.setProducts(request.getShoppingCart().getProducts());

        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.isFragile());

        return order;
    }
}
