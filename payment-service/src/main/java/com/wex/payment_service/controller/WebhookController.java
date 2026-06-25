package com.wex.payment_service.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.wex.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks/stripe")
public class WebhookController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handleWebhookEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String header) {
        return paymentService.handleWebhookEvent(payload, header);
    }
}
