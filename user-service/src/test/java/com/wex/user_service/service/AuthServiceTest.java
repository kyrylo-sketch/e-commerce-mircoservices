package com.wex.user_service.service;


import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.UserRepository;
import com.wex.user_service.security.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JWTService jwtService;

    @Mock
    RefreshTokenService refreshTokenService;

    @InjectMocks
    AuthService authService;

    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("name");
        user.setEmail("email");
        user.setPassword("password");
    }

    @Test
    void register_whenEmailNotAlreadyExists(){
        when(userRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(new RefreshToken());
        when(jwtService.generateToken(any())).thenReturn("token");

        ResponseEntity<AuthService.Result> register = authService.register(user);
        AuthService.Result result = register.getBody();

        assertEquals(new AuthService.Result("token", new RefreshToken().getToken(), user.getId()), result);
        verify(userRepository, times(1)).findByEmail("email");
        verify(userRepository, times(1)).save(user);
        verify(refreshTokenService, times(1)).createRefreshToken(user);
        verify(jwtService, times(1)).generateToken(any());

    }

    @Test
    void register_whenEmailAlreadyExists(){
        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));

        ResponseEntity<AuthService.Result> register = authService.register(user);
        AuthService.Result result = register.getBody();

        assertEquals(new AuthService.Result("fail", "fail",0), result);
        verify(userRepository, times(1)).findByEmail("email");
    }

    @Test
    void login_whenUserIsAuthenticated() {

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);
        when(jwtService.generateToken(any())).thenReturn("token");


        ResponseEntity<AuthService.Result> login = authService.login(user);
        AuthService.Result result = login.getBody();

        assertNotNull(result);
        assertEquals("token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
//        assertEquals(user, result.customer());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(user.getEmail());
        verify(refreshTokenService).createRefreshToken(user);
        verify(jwtService).generateToken(any());

    }

    @Test
    void login_whenUserIsNotAuthenticated() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));

        ResponseEntity<AuthService.Result> login = authService.login(user);
        AuthService.Result result = login.getBody();

        assertEquals(new AuthService.Result("fail", "fail", 0), result);
        verify(userRepository, never()).findByEmail(user.getEmail());

    }
}
