package com.wex.user_service.controller;


import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import com.wex.user_service.security.JWTService;
import com.wex.user_service.service.AuthService;
import com.wex.user_service.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    JWTService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthService.Result> register(@RequestBody User user){
        System.out.println("REGISTER REQUEST");
        return authService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.Result> login(@RequestBody User user){
        return authService.login(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body){
        String token = body.get("refreshToken");
        RefreshToken refreshToken = refreshTokenService.findByToken(token);
        if(refreshToken == null || !refreshTokenService.isValid(refreshToken)){
            return new ResponseEntity<>("Token invalid", HttpStatus.BAD_REQUEST);
        }
        else{
            String newAccessToken = jwtService.generateToken(refreshToken.getUser().getEmail());
            return new ResponseEntity<>(Map.of("accessToken", newAccessToken), HttpStatus.OK);
        }
    }


}
