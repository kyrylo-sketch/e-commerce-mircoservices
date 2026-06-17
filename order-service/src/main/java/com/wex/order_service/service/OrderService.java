package com.wex.order_service.service;

import com.wex.order_service.feign.OrderInterface;
import com.wex.order_service.model.Order;
import com.wex.order_service.model.OrderItem;
import com.wex.order_service.model.OrderWrapper;
import com.wex.order_service.model.Status;
import com.wex.order_service.repository.OrderItemRepository;
import com.wex.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderInterface orderInterface;

    public ResponseEntity<List<Order>> findAllOrders() {
        return new ResponseEntity<>(orderRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Order> findById(int id) {
        return new ResponseEntity<>(orderRepository.findById(id).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<List<Order>> findAllByStatus(Status status) {
        return new ResponseEntity<>(orderRepository.findByStatus(status), HttpStatus.OK);
    }

    public ResponseEntity<Order> saveOrder(Order order) {
        Order saved = orderRepository.save(order);
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    public ResponseEntity<Order> updateOrder(Order order) {
        Order updated = orderRepository.save(order);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteOrder(int id) {
        orderRepository.deleteById(id);
        return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<OrderItem> addProductToOrder(String productId, int quantity, int orderId) {
        OrderItem orderItem = orderInterface.addToOrder(productId, quantity).getBody();
        OrderItem saved = orderItemRepository.save(orderItem);
        Order order = orderRepository.findById(orderId).orElse(null);
        order.setPrice(order.getPrice() + orderItem.getPrice());

        order.getItems().add(saved);
        orderRepository.save(order);
        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }

    public ResponseEntity<List<OrderWrapper>> getUsersOrders(List<Integer> orderIds) {
        List<Order> orders = new ArrayList<>();
        for (Integer orderId : orderIds) {
            orders.add(orderRepository.findById(orderId).orElse(null));
        }
        List<OrderWrapper> orderWrappers = new ArrayList<>();
        for (Order order : orders) {
            OrderWrapper orderWrapper = new OrderWrapper();
            orderWrapper.setOrderId(order.getId());
            orderWrapper.setStatus(order.getStatus());
            orderWrapper.setPrice(order.getPrice());
            orderWrappers.add(orderWrapper);
        }

        return new ResponseEntity<>(orderWrappers, HttpStatus.OK);
    }

    public ResponseEntity<List<OrderItem>> getOrderItems(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        List<OrderItem> orderItems = order.getItems();

        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }
}
