package com.wex.order_service.kafka;

import com.wex.order_service.model.Address;
import com.wex.order_service.model.Notification;
import com.wex.order_service.model.Order;
import com.wex.order_service.model.OrderRequest;
import com.wex.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderProducer {


//    @Autowired
//    private OrderService orderService;
//
//    @PostMapping
//    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest order) {
//        return orderService.saveOrder(order);
//    }
}
