package com.wex.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderRequest {
    private Notification notification;
    private int orderId;
    private Status status;
    private double price;
    private Address shippingAddress;
}
