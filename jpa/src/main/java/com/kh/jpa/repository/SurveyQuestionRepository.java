package com.kh.jpa.repository;

import com.kh.jpa.entity.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    Optional<SurveyQuestion> findByqId(String qId); // 프론트 UUID로 조회
}