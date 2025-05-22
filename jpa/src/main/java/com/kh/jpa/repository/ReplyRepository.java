package com.kh.jpa.repository;

import com.kh.jpa.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> { // PK 타입이 Long
    // 특정 게시글(Board)에 속한 모든 댓글 조회 (생성일 순으로 정렬)
    List<Reply> findByBoardBoardNoOrderByCreateDateAsc(Long boardNo);

    // 특정 게시글에 속한 댓글 수
    long countByBoardBoardNo(Long boardNo);
}