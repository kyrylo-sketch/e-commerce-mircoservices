package com.wex.payment_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderRequest {
    private Long quantity;
    private String currency;
    private String name;
    private Long unitAmount;
    private int paymentId;

}
