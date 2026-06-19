package com.wex.notification_service.controller;

import com.wex.notification_service.model.OrderRequest;
import com.wex.notification_service.model.PaymentRequest;
import com.wex.notification_service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/order")
    public ResponseEntity<String> sendMailAboutOrder(@RequestBody OrderRequest orderRequest) {
        return  notificationService.sendMailAboutOrder(orderRequest);
    }

    @PostMapping("/payment")
    public ResponseEntity<String> sendMailAboutPayment(@RequestBody PaymentRequest paymentRequest) {
        return  notificationService.sendMailAboutPayment(paymentRequest);
    }


}
