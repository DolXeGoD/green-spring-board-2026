package com.green.spring_board.repository;

import com.green.spring_board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByUserIdAndIsDeletedFalse(int userId);
    Page<Board> findByIsDeletedFalse(Pageable pageable);
}
