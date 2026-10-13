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

    // JPQL를 이용해 해당 메서드가 실행할 쿼리 직접 명시
    // 메서드 이름만으로 표현하기 어려운 복잡한 쿼리가 필요한 경우 사용
    // FULLTEXT 인덱스를 활용하기 위해 MATCH(컬럼) AGAINST(키워드) 구문 활용
    // 또한 ngram parser가 적용되어 있으므로 검색 목적에 따라
    // 자연어 모드(NATURAL LANGUAGE MODE), 불리언 모드(BOOLEAN MODE) 등 검색 모드를 적절히 지정
    @Query(value = "SELECT * FROM boards WHERE is_deleted = false AND " +
            "MATCH(title) AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    List<Board> searchByTitle(@Param("keyword") String keyword);
}
