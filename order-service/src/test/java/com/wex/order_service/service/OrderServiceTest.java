package com.wex.order_service.service;


import com.wex.order_service.feign.OrderInterface;
import com.wex.order_service.model.*;
import com.wex.order_service.repository.OrderItemRepository;
import com.wex.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderInterface orderInterface;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldFindAllOrders() {
        Order order = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order));

        ResponseEntity<List<Order>> response = orderService.findAllOrders();

        assertEquals(1, response.getBody().size());
        verify(orderRepository).findAll();
    }

    @Test
    void shouldFindById() {
        Order order = new Order();
        order.setId(1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        ResponseEntity<Order> response = orderService.findById(1);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());

        verify(orderRepository).findById(1);
    }

    @Test
    void shouldFindAllByStatus() {
        Order order = new Order();
        Status status = Status.PAID;

        when(orderRepository.findByStatus(status)).thenReturn(List.of(order));

        ResponseEntity<List<Order>> response = orderService.findAllByStatus(status);

        assertEquals(1, response.getBody().size());
        verify(orderRepository).findByStatus(status);
    }

    @Test
    void shouldSaveOrder() {
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);

        ResponseEntity<Order> response = orderService.saveOrder(order);

        assertNotNull(response.getBody());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldUpdateOrder() {
        Order order = new Order();
        when(orderRepository.save(order)).thenReturn(order);

        ResponseEntity<Order> response = orderService.updateOrder(order);

        assertNotNull(response.getBody());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldDeleteOrder() {
        doNothing().when(orderRepository).deleteById(1);

        ResponseEntity<String> response = orderService.deleteOrder(1);

        assertEquals("Order deleted successfully", response.getBody());
        verify(orderRepository).deleteById(1);
    }

    @Test
    void shouldAddProductToOrder() {
        String productId = "p1";

        OrderItem orderItem = new OrderItem();
        orderItem.setPrice(100);

        Order order = new Order();
        order.setId(1);
        order.setPrice(200);
        order.setItems(new ArrayList<>());

        when(orderInterface.addToOrder(productId, 2))
                .thenReturn(ResponseEntity.ok(orderItem));

        when(orderItemRepository.save(orderItem)).thenReturn(orderItem);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        ResponseEntity<OrderItem> response =
                orderService.addProductToOrder(productId, 2, 1);

        assertNotNull(response.getBody());
        assertEquals(100, response.getBody().getPrice());
        assertEquals(300, order.getPrice()); // 200 + 100

        verify(orderInterface).addToOrder(productId, 2);
        verify(orderItemRepository).save(orderItem);
        verify(orderRepository).save(order);
    }

    @Test
    void shouldGetUsersOrders() {
        Order order = new Order();
        order.setId(1);
        order.setPrice(100);
        order.setStatus(Status.PAID);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        ResponseEntity<List<OrderWrapper>> response =
                orderService.getUsersOrders(List.of(1));

        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getOrderId());

        verify(orderRepository).findById(1);
    }

    @Test
    void shouldGetOrderItems() {
        OrderItem item = new OrderItem();

        Order order = new Order();
        order.setItems(List.of(item));

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        ResponseEntity<List<OrderItem>> response =
                orderService.getOrderItems(1);

        assertEquals(1, response.getBody().size());
        verify(orderRepository).findById(1);
    }
}