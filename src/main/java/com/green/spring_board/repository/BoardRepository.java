package com.green.spring_board.repository;

import com.green.spring_board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByUserIdAndIsDeletedFalse(int userId);

    Page<Board> findByIsDeletedFalse(Pageable pageable);

    @Query(value = "SELECT * FROM boards WHERE is_deleted = false AND " +
            "MATCH(title) AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    List<Board> searchByTitle(@Param("keyword") String keyword);
}
