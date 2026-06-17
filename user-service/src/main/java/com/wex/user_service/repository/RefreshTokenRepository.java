package com.wex.user_service.repository;

import com.wex.user_service.model.RefreshToken;
import com.wex.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    RefreshToken findByToken(String token);
    RefreshToken findByUser(User user);
}
