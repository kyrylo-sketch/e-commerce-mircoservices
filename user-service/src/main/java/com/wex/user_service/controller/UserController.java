package com.wex.user_service.controller;

import com.wex.user_service.model.OrderWrapper;
import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import com.wex.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
//@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> findUserById(@PathVariable int userId) {
        return userService.findUserById(userId);
    }

    @PostMapping("/orders/{userId}")
    public ResponseEntity<List<OrderWrapper>> usersOrders(@PathVariable int userId) {
        return userService.findUsersOrders(userId);
    }

    @PostMapping("/addOrder/{userId}/{orderId}")
    public ResponseEntity<User> addOrder(@PathVariable int orderId, @PathVariable int userId) {
        return userService.addOrder(userId, orderId);
    }


}
