package org.example.foodanddrinkproject.dto;

import org.example.foodanddrinkproject.enums.OrderStatus;
import org.example.foodanddrinkproject.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateOrderRequest {
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
}