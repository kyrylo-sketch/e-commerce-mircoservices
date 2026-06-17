package com.wex.user_service.service;

import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import com.wex.user_service.repository.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {


    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    public RefreshToken createRefreshToken(User customer){
        log.info("Creating refresh token request: customerId={}", customer.getId());
        RefreshToken existing = refreshTokenRepo.findByUser(customer);
        if(existing != null){
            refreshTokenRepo.delete(existing);
            log.info("Refresh token has been deleted");
        }

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryAt(LocalDateTime.now().plusDays(7));
        rt.setUser(customer);
        log.info("Created refresh token successful: customerId={}", customer.getId());
        return refreshTokenRepo.save(rt);
    }

    public boolean isValid(RefreshToken rt) {
        log.info("Validating refresh token request: customerId={}", rt.getUser().getId());
        return rt.getExpiryAt().isAfter(LocalDateTime.now());
    }

    public RefreshToken findByToken(String token){
        log.info("Finding refresh token request: token={}", token);
        return refreshTokenRepo.findByToken(token);
    }
}
