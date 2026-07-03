package ru.yandex.practicum.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.math.BigDecimal;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PaymentMapper {

    @Mapping(source = "totalPrice", target = "totalPayment")
    @Mapping(source = "deliveryPrice", target = "deliveryTotal")
    @Mapping(source = "productPrice", target = "productTotal")
    Payment toPayment(OrderDto orderDto);

    @Mapping(target = "feeTotal", expression = "java(calculateFeeTotal(payment))")
    PaymentDto toPaymentDto(Payment payment);

    default BigDecimal calculateFeeTotal(Payment payment) {
        if (payment == null ||
                payment.getTotalPayment() == null ||
                payment.getProductTotal() == null ||
                payment.getDeliveryTotal() == null) {
            return null;
        }
        return payment.getTotalPayment()
                .subtract(payment.getProductTotal())
                .subtract(payment.getDeliveryTotal());
    }
}
