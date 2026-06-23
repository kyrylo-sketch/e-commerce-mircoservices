package com.wex.order_service.service;

import com.wex.order_service.feign.NotificationInteface;
import com.wex.order_service.feign.OrderInterface;
import com.wex.order_service.model.*;
import com.wex.order_service.repository.OrderItemRepository;
import com.wex.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderInterface orderInterface;

    @Autowired
    private NotificationInteface notificationInteface;

    @Autowired
    private KafkaTemplate<String, OrderRequest> kafkaTemplate;

    public ResponseEntity<List<Order>> findAllOrders() {
        log.info("Finding all Orders request");
        return new ResponseEntity<>(orderRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<Order> findById(int id) {
        log.info("Finding Order request with id {}", id);
        return new ResponseEntity<>(orderRepository.findById(id).orElse(null), HttpStatus.OK);
    }

    public ResponseEntity<List<Order>> findAllByStatus(Status status) {
        log.info("Finding all Orders request with status {}", status);
        return new ResponseEntity<>(orderRepository.findByStatus(status), HttpStatus.OK);
    }

    public ResponseEntity<Order> saveOrder(OrderRequest orderRequest) {
        log.info("Saving Order request, order {}", orderRequest.toString());

        Order order = new Order();

        order.setPrice(orderRequest.getPrice());
        order.setStatus(orderRequest.getStatus());
        order.setShippingAddress(orderRequest.getShippingAddress());
        Order saved = orderRepository.save(order);
        orderRequest.setOrderId(saved.getId());

        kafkaTemplate.send("order-creating", orderRequest);
        log.info("Saving Order request successfully, order {}", saved.toString());
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    public ResponseEntity<Order> updateOrder(OrderRequest orderRequest) {
        log.info("Updating Order request, order {}", orderRequest.toString());

        Order order = new Order();

        order.setStatus(orderRequest.getStatus());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPrice(orderRequest.getPrice());
        Order updated = orderRepository.save(order);
        orderRequest.setOrderId(updated.getId());

        kafkaTemplate.send("order-status-changed", orderRequest);
        log.info("Updating Order request successfully, order {}", updated.toString());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteOrder(int id) {
        log.info("Deleting Order request, orderId {}", id);
        orderRepository.deleteById(id);
        log.info("Deleting Order request successfully, orderId {}", id);
        return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
    }

    public ResponseEntity<OrderItem> addProductToOrder(String productId, int quantity, int orderId) {
        log.info("Adding Product to Order request, orderId{}, quantity{}, productId{},", orderId, quantity, productId);
        OrderItem orderItem = orderInterface.addToOrder(productId, quantity).getBody();
        if(orderItem == null){
            log.error("Order Item is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        OrderItem saved = orderItemRepository.save(orderItem);
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            log.error("Order Item is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        order.setPrice(order.getPrice() + orderItem.getPrice());

        order.getItems().add(saved);
        orderRepository.save(order);
        log.info("Adding Product to Order successfully, orderId{}", orderId);
        return new ResponseEntity<>(orderItem, HttpStatus.OK);
    }

    public ResponseEntity<List<OrderWrapper>> getUsersOrders(List<Integer> orderIds) {
        log.info("Getting Users Orders request");
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

        log.info("Getting Users Orders successfully");
        return new ResponseEntity<>(orderWrappers, HttpStatus.OK);
    }

    public ResponseEntity<List<OrderItem>> getOrderItems(int orderId) {
        log.info("Getting Order Items request");
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            log.error("Order Item is null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<OrderItem> orderItems = order.getItems();

        log.info("Getting Order Items successfully");
        return new ResponseEntity<>(orderItems, HttpStatus.OK);
    }
}
