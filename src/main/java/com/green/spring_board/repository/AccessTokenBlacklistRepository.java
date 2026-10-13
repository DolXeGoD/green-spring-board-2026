package com.green.spring_board.repository;

import com.green.spring_board.entity.AccessTokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccessTokenBlacklistRepository
        extends JpaRepository<AccessTokenBlacklist, Integer> {
    Optional<AccessTokenBlacklist> findByToken(String token);

    // expirationDatetime이 매개변수의 시간보다 이전인 모든 데이터를 삭제한다
    void deleteAllByExpirationDateTimeBefore(LocalDateTime dateTime);
}

