package com.kh.jpa.repository;

import com.kh.jpa.entity.Board; // 추가
import com.kh.jpa.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> { // PK 타입 Long

    // 특정 게시글(Board 객체)에 속한 모든 댓글 조회 (생성일 순으로 정렬)
    List<Reply> findByBoardOrderByCreateDateAsc(Board board);

    // 기존 메서드 (Board의 PK로 조회) - 위 메서드와 기능적으로 유사, 선택적 사용
    List<Reply> findByBoardBoardNoOrderByCreateDateAsc(Long boardNo);

    // 특정 게시글에 속한 댓글 수 (Board 객체 사용)
    long countByBoard(Board board);

    // 기존 메서드 (Board의 PK로 조회)
    long countByBoardBoardNo(Long boardNo);
}