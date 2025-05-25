package com.kh.jpa.service;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.SurveyDto;
import org.springframework.data.domain.Pageable;

public interface SurveyService {
    SurveyDto.Response createSurvey(SurveyDto.Create createDto, String userId);
    PageResponse<SurveyDto.Response> getAllSurveys(Pageable pageable);
    PageResponse<SurveyDto.Response> getHotSurveys(Pageable pageable);
    SurveyDto.Response getSurveyById(Long surveyId); // 응답 제출 폼 표시용
    SurveyDto.Response getSurveyResultsById(Long surveyId); // 결과 보기용 (동일 DTO 사용)
    SurveyDto.Response updateSurvey(Long surveyId, SurveyDto.Update updateDto, String userId);
    void deleteSurvey(Long surveyId, String userId);
    SurveyDto.Response submitSurveyResponse(Long surveyId, SurveyDto.SubmitRequest submitRequest, String userId);
}