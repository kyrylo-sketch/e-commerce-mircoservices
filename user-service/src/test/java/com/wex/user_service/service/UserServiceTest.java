package com.wex.user_service.service;

import com.wex.user_service.feign.UserInterface;
import com.wex.user_service.model.OrderWrapper;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserInterface userInterface;

    @InjectMocks
    UserService userService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        user.setRole("USER");
        user.getOrderIds().add(1);
    }

    @Test
    void findAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        ResponseEntity<List<User>> allUsers = userService.findAllUsers();

        assertNotNull(allUsers);
        assertEquals(allUsers.getBody().size(), 1);
    }

    @Test
    void findUserById_shouldReturnUser() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        ResponseEntity<User> result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(result.getBody().getId(), 1);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void findById_whenUserNotFound_shouldReturnNotFound() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<User> result = userService.findUserById(1);

        assertNotNull(result);
        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void findUsersOrders_shouldReturnAllUsersOrders() {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userInterface.getUsersOrders(any())).thenReturn(new ResponseEntity<>(List.of(new OrderWrapper()), HttpStatus.OK));

        ResponseEntity<List<OrderWrapper>> result = userService.findUsersOrders(user.getId());

        assertNotNull(result);
        assertEquals(result.getBody().size(), 1);
    }

    @Test
    void addOrder_shouldAddOrder() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<User> result = userService.addOrder(1, 1);

        assertNotNull(result);
        assertEquals(result.getBody().getId(), 1);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(user.getOrderIds().size(), 2);
    }


}
