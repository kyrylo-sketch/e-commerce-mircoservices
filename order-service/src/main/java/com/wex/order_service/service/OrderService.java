package com.wex.order_service.service;

import com.wex.order_service.feign.NotificationInteface;
import com.wex.order_service.feign.OrderInterface;
import com.wex.order_service.model.*;
import com.wex.order_service.repository.OrderItemRepository;
import com.wex.order_service.repository.OrderRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        order.setUserEmail(orderRequest.getNotification().getSendToEmail());
        Order saved = orderRepository.save(order);
        orderRequest.setOrderId(saved.getId());

        kafkaTemplate.send("order-creating", orderRequest);
        log.info("Saving Order request successfully, order {}", saved.toString());
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    public ResponseEntity<Order> updateOrder(OrderRequest orderRequest) {
        log.info("Updating Order request, order {}", orderRequest.toString());

        Order order = orderRepository.findById(orderRequest.getOrderId())
                .orElse(null);

        if (order == null) {
            log.error("Order not found, orderId {}", orderRequest.getOrderId());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        order.setStatus(orderRequest.getStatus());

        Order updated = orderRepository.save(order);

        orderRequest.setNotification(new Notification(
                "Status zamówienia #" + order.getId() + " zmieniony",
                "Twoje zamówienie zmieniło status na: " + orderRequest.getStatus(),
                order.getUserEmail()
        ));

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
        try {
            ResponseEntity<OrderItem> response = orderInterface.addToOrder(productId, quantity);
            OrderItem orderItem = response.getBody();
            if (orderItem == null) {
                log.error("Order Item is null");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            OrderItem saved = orderItemRepository.save(orderItem);
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.error("Order Item is null");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            order.setPrice(order.getPrice() + orderItem.getPrice());

            order.getItems().add(saved);
            orderRepository.save(order);
            log.info("Adding Product to Order successfully, orderId{}", orderId);
            return new ResponseEntity<>(orderItem, HttpStatus.OK);
        } catch (FeignException.BadRequest e) {
            log.error("Product unavailable: productId={}, quantity={}", productId, quantity);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 zamiast 500
        } catch (FeignException.NotFound e) {
            log.error("Product not found: productId={}", productId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

    public ResponseEntity<List<OrderWrapper>> getUsersOrders(List<Integer> orderIds) {
        log.info("Getting Users Orders request");
        List<OrderWrapper> orderWrappers = new ArrayList<>();

        for (Integer orderId : orderIds) {
            orderRepository.findById(orderId).ifPresent(order -> {
                OrderWrapper wrapper = new OrderWrapper();
                wrapper.setOrderId(order.getId());
                wrapper.setStatus(order.getStatus());
                wrapper.setPrice(order.getPrice());
                orderWrappers.add(wrapper);
            });
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

    public void updateStatusOrder(PaymentRequest paymentRequest) {
        log.info("Updating Order Status request");
        Order order = findById(paymentRequest.getOrderId()).getBody();
        if (order == null) {
            log.error("Order not found, orderId {}", paymentRequest.getOrderId());
        }else {
            if (Objects.requireNonNull(paymentRequest.getStatus()) == StatusPayment.SUCCESS) {
                order.setStatus(Status.PAID);
            }
            Order saved = orderRepository.save(order);

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setOrderId(saved.getId());
            orderRequest.setStatus(Status.PAID);
            orderRequest.setPrice(saved.getPrice());
            orderRequest.setShippingAddress(saved.getShippingAddress());
            orderRequest.setNotification(new Notification(
                    "Status zamówienia #" + saved.getId() + " zmieniony",
                    "Twoje zamówienie zmieniło status na: " + saved.getStatus(),
                    saved.getUserEmail()
            ));
            kafkaTemplate.send("order-status-changed", orderRequest);
            log.info("Updating Order Status successfully");
        }

    }
}
