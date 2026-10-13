package com.green.spring_board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_token_blacklist")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenBlacklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "token", nullable = false, length = 1024)
    private String token;

    @Column(name = "expiration_datetime", nullable = false)
    private LocalDateTime expirationDateTime;
}
