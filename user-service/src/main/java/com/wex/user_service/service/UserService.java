package com.wex.user_service.service;

import com.wex.user_service.feign.UserInterface;
import com.wex.user_service.model.OrderWrapper;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInterface userInterface;

    public ResponseEntity<List<User>> findAllUsers() {
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<User> findUserById(int id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<List<OrderWrapper>> findUsersOrders(int userId) {
        User user = findUserById(userId).getBody();
        List<OrderWrapper> userOrders = userInterface.getUsersOrders(user.getOrderIds()).getBody();

        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    public ResponseEntity<User> addOrder(int userId, int orderId) {
        User user = findUserById(userId).getBody();
        if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        user.getOrderIds().add(orderId);
        User saved = userRepository.save(user);
        return new ResponseEntity<>(saved ,HttpStatus.OK);
    }
}
