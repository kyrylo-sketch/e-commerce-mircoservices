package com.wex.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class PaymentRequest {
    private Notification notification;
    private Status status;
    private double amount;
    private LocalDateTime paymentDate;
    private int orderId;

    public PaymentRequest(Notification notification, Status status, double amount, int orderId) {
        this.notification = notification;
        this.status = status;
        this.amount = amount;
        this.orderId = orderId;
    }
}
