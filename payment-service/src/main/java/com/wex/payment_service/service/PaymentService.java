package com.wex.payment_service.service;

import com.wex.payment_service.controller.PaymentController;
import com.wex.payment_service.model.Payment;
import com.wex.payment_service.model.PaymentRequest;
import com.wex.payment_service.model.Status;
import com.wex.payment_service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, PaymentRequest> kafkaTemplate;

    public ResponseEntity<Payment> savePayment(PaymentRequest paymentRequest) {
        log.info("Saving request  Payment {}", paymentRequest.toString());
        Payment payment = new Payment();
        payment.setStatus(paymentRequest.getStatus());
        payment.setAmount(paymentRequest.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOrderId(paymentRequest.getOrderId());

        Payment saved = paymentRepository.save(payment);

        kafkaTemplate.send("payment-events", paymentRequest);
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
}
