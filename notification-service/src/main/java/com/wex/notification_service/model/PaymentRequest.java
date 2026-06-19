package com.wex.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentRequest {
    private Notification notification;
    private StatusPayment status;
    private double amount;
    private LocalDateTime paymentDate;
    private int orderId;
}
