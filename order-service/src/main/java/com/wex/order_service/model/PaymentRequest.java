package com.wex.order_service.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime paymentDate;
    private int orderId;
}
