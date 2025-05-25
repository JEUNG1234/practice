package com.kh.jpa.repository;

import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findAllByOrderByCreateDateDesc(Pageable pageable);

    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.boardNo = :boardNo")
    int incrementViewCount(@Param("boardNo") Long boardNo);

    // Mypage.jsx에서 작성자 이름으로 게시글 목록을 요청하므로, Member의 userName으로 검색
    @Query("SELECT b FROM Board b WHERE b.member.userName = :authorName ORDER BY b.createDate DESC")
    Page<Board> findByMemberUserName(@Param("authorName") String authorName, Pageable pageable);
}