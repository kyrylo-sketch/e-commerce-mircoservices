package com.wex.payment_service.controller;

import com.wex.payment_service.model.Payment;
import com.wex.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
//@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return paymentService.savePayment(payment);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<Payment>> getPaymentById(@PathVariable int orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<Payment>  addOrderToPayment(@PathVariable int orderId, @RequestParam int       paymentId) {
        return paymentService.addOrderToPayment(paymentId, orderId);
    }
}
