package com.wex.payment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Status status;
    private String cardNumber;
    private double amount;
    private LocalDateTime paymentDate;
    private int orderId;

    public Payment(double amount, String cardNumber, Status status, int orderId) {
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.status = status;
        paymentDate = LocalDateTime.now();
        this.orderId = orderId;
    }
}
