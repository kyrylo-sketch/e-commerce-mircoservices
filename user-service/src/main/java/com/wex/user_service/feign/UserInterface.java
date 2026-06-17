package com.wex.user_service.feign;

import com.wex.user_service.model.OrderWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("ORDER-SERVICE")
public interface UserInterface {
    @PostMapping("/api/orders/userOrders")
    public ResponseEntity<List<OrderWrapper>> getUsersOrders(@RequestBody List<Integer> orderIds);
}
