package com.wex.payment_service.controller;

import com.stripe.exception.StripeException;
import com.wex.payment_service.model.OrderRequest;
import com.wex.payment_service.model.Payment;
import com.wex.payment_service.model.PaymentRequest;
import com.wex.payment_service.model.Status;
import com.wex.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Stack;

@RestController
@RequestMapping("/api/payments")
//@CrossOrigin
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest payment) {
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

    @GetMapping("/status/{orderId}")
    public ResponseEntity<Status> getPaymentStatus(@PathVariable int orderId) {
        return paymentService.getPaymentStatus(orderId);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayment(@RequestBody Map<String, Object> request) throws StripeException {
        return paymentService.createPayment(request);
    }

    @PostMapping("/hostedCheckout")
    public ResponseEntity<Map<String, String>> hostedCheckout(@RequestBody OrderRequest orderRequest) throws StripeException {
        return paymentService.hostedCheckout(orderRequest);
    }


}
