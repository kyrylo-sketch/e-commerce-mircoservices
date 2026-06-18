package com.wex.payment_service.repository;

import com.wex.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    public Payment findPaymentByOrderId(Integer orderId);
    public List<Payment> getPaymentByOrderId(int orderId);
}
