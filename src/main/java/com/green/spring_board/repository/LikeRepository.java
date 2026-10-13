package com.green.spring_board.repository;

import com.green.spring_board.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserIdAndBoardId(int userId, int boardId);
    boolean existsByUserIdAndBoardId(int userId, int boardId);
    List<Like> findByBoardId(int boardId);

    @Query("SELECT l.board.id FROM Like l WHERE l.user.id = :userId")
    List<Integer> findLikedBoardIdsByUserId(@Param("userId") int userId);
}
