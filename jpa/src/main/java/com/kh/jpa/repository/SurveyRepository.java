package com.kh.jpa.repository;

import com.kh.jpa.entity.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Page<Survey> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // HOT 설문 (totalRespondents 많은 순, 상위 N개)
    Page<Survey> findAllByOrderByTotalRespondentsDescCreatedAtDesc(Pageable pageable);
}