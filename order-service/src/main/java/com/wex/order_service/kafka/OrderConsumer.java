package com.wex.order_service.kafka;

import com.wex.order_service.model.PaymentRequest;
import com.wex.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "payment-events", groupId = "new-payment-events-group")
    public void listenPaymentEvents(PaymentRequest paymentRequest) {
        orderService.updateStatusOrder(paymentRequest);
    }
}
