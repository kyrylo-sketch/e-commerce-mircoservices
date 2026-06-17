package com.wex.payment_service.service;

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

    public ResponseEntity<String> savePayment(Payment payment) {
        paymentRepository.save(payment);
        return new ResponseEntity<>("Payment saved successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> updatePayment(Payment payment) {
        paymentRepository.save(payment);
        return new ResponseEntity<>("Payment updated successfully", HttpStatus.OK);
    }

    public ResponseEntity<String> deletePayment(int paymentId) {
        paymentRepository.deleteById(paymentId);
        return new ResponseEntity<>("Payment deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getAllPayments() {
        return new ResponseEntity<>(paymentRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Payment> getPaymentById(int paymentId) {
        return new ResponseEntity<>(paymentRepository.findById(paymentId).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<List<Payment>> getPaymentByOrderId(int orderId) {
        return new ResponseEntity<>(paymentRepository.getPaymentByOrderId(orderId), HttpStatus.OK);
    }
}
