package com.wex.order_service.feign;

import com.wex.order_service.model.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("NOTIFICATION-SERVICE")
public interface NotificationInteface {
    @PostMapping("/api/notifications/order")
    public ResponseEntity<String> sendMailAboutOrder(@RequestBody OrderRequest orderRequest);
}
