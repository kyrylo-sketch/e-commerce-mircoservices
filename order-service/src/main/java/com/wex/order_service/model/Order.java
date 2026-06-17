package com.wex.order_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Status status;
    @OneToMany
    private List<OrderItem> items = new ArrayList<>();
    private double price;
    private String shippingAddress;

    public Order(Status status, String shippingAddress) {
        this.status = status;
        this.shippingAddress = shippingAddress;
    }
}
