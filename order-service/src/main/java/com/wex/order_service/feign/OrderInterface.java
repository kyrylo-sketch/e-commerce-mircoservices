package com.wex.order_service.feign;

import com.wex.order_service.model.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PRODUCT-SERVICE")
public interface OrderInterface {
    @PostMapping("/addToOrder")
    public ResponseEntity<OrderItem> addToOrder(@RequestParam String productId, @RequestParam int quantity);

}
