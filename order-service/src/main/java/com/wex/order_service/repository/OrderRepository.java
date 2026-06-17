package com.wex.order_service.repository;

import com.wex.order_service.model.Order;
import com.wex.order_service.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    public List<Order> findByStatus(Status status);
}
