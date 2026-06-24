package com.wex.notification_service.kafka;

import com.wex.notification_service.model.OrderRequest;
import com.wex.notification_service.model.PaymentRequest;
import com.wex.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(
            topics = "order-creating",
            groupId = "order-creating-group",
            containerFactory = "orderKafkaListenerContainerFactory")
    public void listenOrderCreating(OrderRequest orderRequest) {
        notificationService.sendMailAboutOrder(orderRequest);
    }

    @KafkaListener(
            topics = "order-status-changed",
            groupId = "order-status-changed-group",
            containerFactory = "orderKafkaListenerContainerFactory")
    public void listenOrderStatusChanged(OrderRequest orderRequest) {
        notificationService.sendMailAboutOrder(orderRequest);
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "payment-events-group",
            containerFactory = "paymentKafkaListenerContainerFactory")
    public void listenPaymentEvent(PaymentRequest paymentRequest) {
        notificationService.sendMailAboutPayment(paymentRequest);
    }

}
