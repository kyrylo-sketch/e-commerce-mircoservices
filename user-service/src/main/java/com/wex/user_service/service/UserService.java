package com.wex.user_service.service;

import com.wex.user_service.feign.UserInterface;
import com.wex.user_service.model.OrderWrapper;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInterface userInterface;

    public ResponseEntity<List<User>> findAllUsers() {
        log.info("Finding all users");
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<User> findUserById(int id) {
        log.info("Finding user request, userId{}", id);
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            log.info("User not found, userId{}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        log.info("User found, userId{}", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<List<OrderWrapper>> findUsersOrders(int userId) {
        log.info("Finding orders request for userId {}", userId);
        User user = findUserById(userId).getBody();
        List<OrderWrapper> userOrders = userInterface.getUsersOrders(user.getOrderIds()).getBody();
        log.info("Finding orders successfully, userId {}", userId);
        return new ResponseEntity<>(userOrders, HttpStatus.OK);
    }

    public ResponseEntity<User> addOrder(int userId, int orderId) {
        log.info("Adding order request for userId {}, orderId {}", userId, orderId);
        User user = findUserById(userId).getBody();
        if(user == null){
            log.info("User not found, userId {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.getOrderIds().add(orderId);
        User saved = userRepository.save(user);
        log.info("Adding order successfully, userId {}", userId);
        return new ResponseEntity<>(saved ,HttpStatus.OK);
    }
}
