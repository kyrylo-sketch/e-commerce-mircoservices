package com.wex.payment_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Status status;
    private double amount;
    private LocalDateTime paymentDate;
    private int orderId;

    public Payment(double amount, Status status, int orderId) {
        this.amount = amount;
        this.status = status;
        paymentDate = LocalDateTime.now();
        this.orderId = orderId;
    }

}
