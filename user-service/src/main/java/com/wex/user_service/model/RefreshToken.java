package com.wex.user_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String token;
    private LocalDateTime expiryAt;
    @OneToOne
    private User user;

    public RefreshToken(String token, LocalDateTime expiryAt, User user) {
        this.token = token;
        this.expiryAt = expiryAt;
        this.user = user;
    }


}
