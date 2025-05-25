package com.kh.jpa.repository;

import com.kh.jpa.entity.SurveyOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurveyOptionRepository extends JpaRepository<SurveyOption, Long> {
    Optional<SurveyOption> findByOptId(String optId); // 프론트 UUID로 조회
}