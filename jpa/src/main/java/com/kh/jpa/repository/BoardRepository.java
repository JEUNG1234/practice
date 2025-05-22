package com.kh.jpa.repository;

import com.kh.jpa.entity.Board;
import org.springframework.data.domain.Page; // Spring Data Page import
import org.springframework.data.domain.Pageable; // Spring Data Pageable import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> { // PK 타입이 Long
    // 상태(Y/N) 와 관계없이 페이징 처리하여 모든 게시글 조회 (최신순)
    // BoardController.java의 getBoards와 유사한 기능
    // 프론트엔드 BoardList.jsx는 현재 별도 상태 필터링 없이 모든 글을 reverse()
    Page<Board> findAllByOrderByCreateDateDesc(Pageable pageable);

    // 조회수 증가
    @Modifying // SELECT 쿼리가 아님을 명시
    @Transactional // 조회수 증가도 트랜잭션 내에서 처리
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardNo = :boardNo")
    int incrementViewCount(@Param("boardNo") Long boardNo);
}