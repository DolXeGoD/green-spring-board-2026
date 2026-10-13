package com.green.spring_board.repository;

import com.green.spring_board.entity.AccessTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccessTokenBlacklistRepository
        extends JpaRepository<AccessTokenBlacklist, Integer> {
    Optional<AccessTokenBlacklist> findByToken(String token);
}

