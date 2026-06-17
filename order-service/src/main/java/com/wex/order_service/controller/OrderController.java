package com.wex.order_service.controller;

import com.wex.order_service.model.Order;
import com.wex.order_service.model.OrderItem;
import com.wex.order_service.model.OrderWrapper;
import com.wex.order_service.model.Status;
import com.wex.order_service.repository.OrderRepository;
import com.wex.order_service.service.OrderService;
import feign.Response;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> findAllOrders() {
        return orderService.findAllOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> findById(@PathVariable int orderId) {
        return orderService.findById(orderId);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @PutMapping
    public ResponseEntity<Order> updateOrder(@RequestBody Order order) {
        return orderService.updateOrder(order);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteOrder(@RequestBody Order order) {
        return orderService.deleteOrder(order.getId());
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<OrderItem> addProductToOrder(@RequestParam String productId,@RequestParam int quantity, @PathVariable int orderId) {
        return orderService.addProductToOrder(productId, quantity,  orderId);
    }

    @PostMapping("/userOrders")
    public ResponseEntity<List<OrderWrapper>> getUsersOrders(@RequestBody List<Integer> orderIds) {
        return orderService.getUsersOrders(orderIds);
    }

    @GetMapping("/orderItems/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable int orderId) {
        return orderService.getOrderItems(orderId);
    }

}
