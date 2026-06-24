package com.wex.order_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private Status status;
    @OneToMany
    private List<OrderItem> items = new ArrayList<>();
    private double price;
    @OneToOne(cascade = CascadeType.ALL)
    private Address shippingAddress;
    private String userEmail;

    public Order(Status status, Address shippingAddress) {
        this.status = status;
        this.shippingAddress = shippingAddress;
    }
}
