package com.kh.jpa.repository;

import com.kh.jpa.entity.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    // 모든 투표 목록 페이징 (최신순)
    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // HOT 투표 목록 (totalVotes 많은 순, 상위 N개) - Pageable로 N개 제한
    Page<Poll> findAllByOrderByTotalVotesDescCreatedAtDesc(Pageable pageable);
}