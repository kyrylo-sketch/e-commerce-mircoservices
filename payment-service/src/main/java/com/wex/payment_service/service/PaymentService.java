package com.wex.payment_service.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.wex.payment_service.controller.PaymentController;
import com.wex.payment_service.model.*;
import com.wex.payment_service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String webhookSecret;

    public ResponseEntity<Payment> savePayment(PaymentRequest paymentRequest) {
        log.info("Saving request  Payment {}", paymentRequest.toString());
        Payment payment = new Payment();
        payment.setStatus(paymentRequest.getStatus());//
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setEmailSendTo(paymentRequest.getNotification().getSendToEmail());

        Payment saved = paymentRepository.save(payment);

        paymentRequest.setPaymentDate(saved.getPaymentDate());

//        kafkaTemplate.send("payment-events", paymentRequest);
        log.info("Saving request successful Payment {}", saved.toString());

        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    public ResponseEntity<Payment> updatePayment(Payment payment) {
        log.info("Updating request  Payment {}", payment.toString());
        Payment updated = paymentRepository.save(payment);
        log.info("Updating request successful Payment {}", updated.toString());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    public ResponseEntity<String> deletePayment(int paymentId) {
        log.info("Deleting request  Payment {}", paymentId);
        paymentRepository.deleteById(paymentId);
        log.info("Deleting request successful Payment {}", paymentId);
        return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getAllPayments() {
        log.info("Getting all payments");
        return new ResponseEntity<>(paymentRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Payment> getPaymentById(int paymentId) {
        log.info("Getting payment by Payment id {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            log.info("Payment not found for Payment id {}", paymentId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Payment found for Payment id {}", paymentId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getPaymentByOrderId(int orderId) {
        log.info("Getting request payment by order id {}", orderId);
        paymentRepository.getPaymentByOrderId(orderId);
        log.info("Getting successful payment by order id {}", orderId);
        return new ResponseEntity<>(paymentRepository.getPaymentByOrderId(orderId), HttpStatus.OK);
    }

    public ResponseEntity<Payment> addOrderToPayment(int paymentId, int orderId) {
        log.info("Adding order to Payment request, paymentId{}, orderId{}", paymentId, orderId);
        Payment payment = getPaymentById(paymentId).getBody();
        if (payment == null){
            log.info("Payment not found for paymentId {}", paymentId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        payment.setOrderId(orderId);
        ResponseEntity<Payment> updated = updatePayment(payment);
        log.info("Payment updated successfully for paymentId{}, orderId{}", paymentId, orderId);
        return new ResponseEntity<>(updated.getBody(), HttpStatus.OK);
    }

    public ResponseEntity<Status> getPaymentStatus(int orderId) {
        log.info("Getting payment status for payment id {}", orderId);
        Payment payment = paymentRepository.findPaymentByOrderId(orderId);
        if (payment == null){
            log.info("Payment not found for paymentId {}", orderId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("Payment found for payment id {}", orderId);
        return new ResponseEntity<>(payment.getStatus(), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> createPayment(Map<String, Object> request) throws StripeException {
        log.info("Creating payment request {}", request.toString());
        Long amount = Long.valueOf(request.get("amount").toString());
        String currency = request.get("currency").toString();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        log.info("Creating payment request successful Payment {}", intent.toString());
        return new ResponseEntity<>(Map.of("clientSecret", intent.getClientSecret()), HttpStatus.CREATED);
    }

    public ResponseEntity<Map<String, String>> hostedCheckout(OrderRequest orderRequest) throws StripeException {
        log.info("Hosting checkout request {}", orderRequest.toString());
        log.info("hostedCheckout request: paymentId={}", orderRequest.getPaymentId());
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:3000/success.html?paymentId=" + orderRequest.getPaymentId())
                        .setCancelUrl("http://localhost:3000/cancel.html")
                        .putMetadata("paymentId", String.valueOf(orderRequest.getPaymentId()))
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(orderRequest.getQuantity())
                                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(orderRequest.getCurrency())
                                                .setUnitAmount(orderRequest.getUnitAmount())
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(orderRequest.getName())
                                                        .build())
                                                .build())
                                        .build()

                        ).build();

        Session session = Session.create(params);

        URI location = URI.create(session.getUrl());
        log.info("Hosting checkout request successful URI {}", location.toString());
        return new ResponseEntity<>(Map.of("url", session.getUrl()), HttpStatus.OK);

    }

    public ResponseEntity<String> handleWebhookEvent(String payload, String header) {
        Event event;
        try{
            event = Webhook.constructEvent(payload, header, webhookSecret);
        }catch (SignatureVerificationException e){
            //invalid signature
            return new ResponseEntity<>("Signature verification failed", HttpStatus.BAD_REQUEST);
        }

        switch (event.getType()){
            case "checkout.session.completed":
                var session = (com.stripe.model.checkout.Session) event
                        .getDataObjectDeserializer().getObject().orElse(null);
                if (session != null) {
                    //  Odczytaj paymentId z metadata
                    String paymentIdStr = session.getMetadata().get("paymentId");
                    int paymentId = Integer.parseInt(paymentIdStr);

                    Payment payment = getPaymentById(paymentId).getBody();
                    if (payment == null) {
                        log.error("Payment not found for id {}", paymentId);
                        break;
                    }

                    payment.setStatus(Status.SUCCESS);
                    updatePayment(payment);

                    PaymentRequest paymentRequest = new PaymentRequest();
                    paymentRequest.setNotification(new Notification(
                            "Płatność zakończona sukcesem",
                            "Twoja płatność " + payment.getAmount() + "$ została zrealizowana.",
                            payment.getEmailSendTo()
                    ));
                    paymentRequest.setStatus(payment.getStatus());
                    paymentRequest.setAmount(payment.getAmount());
                    paymentRequest.setPaymentDate(LocalDateTime.now());
                    paymentRequest.setOrderId(payment.getOrderId());
                    kafkaTemplate.send("payment-events", paymentRequest);
                    log.info("✅ Payment {} updated to SUCCESS", paymentId);
                }
                break;

            case "checkout.session.expired":
                var expiredSession = (com.stripe.model.checkout.Session) event
                        .getDataObjectDeserializer().getObject().orElse(null);
                if (expiredSession != null) {
                    String paymentIdStr = expiredSession.getMetadata().get("paymentId");
                    int paymentId = Integer.parseInt(paymentIdStr);

                    Payment payment = getPaymentById(paymentId).getBody();
                    if (payment != null) {
                        payment.setStatus(Status.FAILED);
                        updatePayment(payment);
                        log.info("❌ Payment {} updated to FAILED", paymentId);
                    }
                }
                break;

            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
