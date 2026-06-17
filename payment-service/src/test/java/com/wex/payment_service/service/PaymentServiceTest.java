package com.wex.payment_service.service;

import com.wex.payment_service.model.Payment;
import com.wex.payment_service.model.Status;
import com.wex.payment_service.repository.PaymentRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    Payment payment;

    @BeforeEach
    public void setUp() {
        payment = new Payment();
        payment.setId(1);
        payment.setOrderId(1);
        payment.setAmount(100);
        payment.setStatus(Status.SUCCESS);
        payment.setCardNumber("123456789");
    }

    @Test
    public void getPaymentById_shouldReturnPayment() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        ResponseEntity<Payment> paymentById = paymentService.getPaymentById(1);

        assertNotNull(paymentById);
        assertEquals(HttpStatus.OK, paymentById.getStatusCode());
        assertEquals(payment, paymentById.getBody());
    }

    @Test
    public void getPaymentById_shouldReturnNotFound() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<Payment> paymentById = paymentService.getPaymentById(1);

        assertNotNull(paymentById);
        assertEquals(HttpStatus.NOT_FOUND, paymentById.getStatusCode());
    }

    @Test
    void getPaymentByOrderId_shouldReturnPayment() {
        when(paymentRepository.getPaymentByOrderId(payment.getOrderId())).thenReturn(List.of(payment));

        ResponseEntity<List<Payment>> paymentByOrderId = paymentService.getPaymentByOrderId(payment.getOrderId());

        assertNotNull(paymentByOrderId);
        assertEquals(HttpStatus.OK, paymentByOrderId.getStatusCode());
    }

    @Test
    void addOrderToPayment_shouldReturnPayment() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        ResponseEntity<Payment> result = paymentService.addOrderToPayment(1, 2);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().getOrderId());
    }

    @Test
    void addOrderToPayment_shouldReturnNotFound() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<Payment> result = paymentService.addOrderToPayment(1, 2);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

}
