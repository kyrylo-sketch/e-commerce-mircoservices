package com.wex.user_service.service;

import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.UserRepository;
import com.wex.user_service.security.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {


//    public record Result(String accessToken, String refreshToken, User customer) {}
        public record Result(String accessToken, String refreshToken, int userId) {}

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);



    public ResponseEntity<Result> register(User customer){
        log.info("Register account request: customerId={}", customer.getId());
        User find = userRepository.findByEmail(customer.getEmail()).orElse(null);
        if(find != null){
            log.warn("Register account failed: email={} already exists", customer.getEmail());
            return new ResponseEntity<>(new Result("fail", "fail",0), HttpStatus.OK);
        }else {
            customer.setPassword(encoder.encode(customer.getPassword()));
            customer.setRole("USER");
            User saved = userRepository.save(customer);
            log.info("Register account success: customerId={}", customer.getId());
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(customer);
            return new ResponseEntity<>(new Result(jwtService.generateToken(customer.getEmail()), refreshToken.getToken(), saved.getId()), HttpStatus.CREATED);
        }

    }

    public ResponseEntity<Result> login(User customer) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(customer.getEmail(), customer.getPassword())
            );
            User fullCustomer = userRepository.findByEmail(customer.getEmail()).orElseThrow();
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(fullCustomer);
            return new ResponseEntity<>(new Result(jwtService.generateToken(customer.getEmail()), refreshToken.getToken(), fullCustomer.getId()), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed: email={}", customer.getEmail());
            return new ResponseEntity<>(new Result("fail", "fail", 0), HttpStatus.BAD_REQUEST);
        }

    }


}
