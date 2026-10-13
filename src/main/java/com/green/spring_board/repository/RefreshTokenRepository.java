package com.green.spring_board.repository;

import com.green.spring_board.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    List<RefreshToken> findByUserId(int userId);
    void deleteByUserId(int userId);
    Optional<RefreshToken> findByToken(String token);

    // expirationDatetime이 매개변수의 시간보다 이전인 모든 데이터를 삭제한다
    void deleteAllByExpirationDateTimeBefore(LocalDateTime dateTime);
}
