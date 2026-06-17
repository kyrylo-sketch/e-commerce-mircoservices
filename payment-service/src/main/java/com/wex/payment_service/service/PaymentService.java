package com.wex.payment_service.service;

import com.wex.payment_service.controller.PaymentController;
import com.wex.payment_service.model.Payment;
import com.wex.payment_service.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public ResponseEntity<Payment> savePayment(Payment payment) {
        Payment saved = paymentRepository.save(payment);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    public ResponseEntity<Payment> updatePayment(Payment payment) {
        Payment updated = paymentRepository.save(payment);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    public ResponseEntity<String> deletePayment(int paymentId) {
        paymentRepository.deleteById(paymentId);
        return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getAllPayments() {
        return new ResponseEntity<>(paymentRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Payment> getPaymentById(int paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getPaymentByOrderId(int orderId) {
        paymentRepository.getPaymentByOrderId(orderId);
        return new ResponseEntity<>(paymentRepository.getPaymentByOrderId(orderId), HttpStatus.OK);
    }

    public ResponseEntity<Payment> addOrderToPayment(int paymentId, int orderId) {
        Payment payment = getPaymentById(paymentId).getBody();
        if (payment == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        payment.setOrderId(orderId);
        ResponseEntity<Payment> updated = updatePayment(payment);
        return new ResponseEntity<>(updated.getBody(), HttpStatus.OK);
    }
}
