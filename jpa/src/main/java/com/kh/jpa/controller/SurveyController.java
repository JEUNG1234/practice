package com.kh.jpa.controller;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.SurveyDto;
import com.kh.jpa.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<SurveyDto.Response> createSurvey(
            @RequestBody SurveyDto.Create createDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SurveyDto.Response responseDto = surveyService.createSurvey(createDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SurveyDto.Response>> getSurveys(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "hot", required = false) boolean hot) {
        if (hot) {
            // MainDashboardPage.jsx 에서 3개만 가져옴
            Pageable hotPageable = PageRequest.of(0, 3, Sort.by("totalRespondents").descending().and(Sort.by("createdAt").descending()));
            return ResponseEntity.ok(surveyService.getHotSurveys(hotPageable));
        }
        return ResponseEntity.ok(surveyService.getAllSurveys(pageable));
    }

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyDto.Response> getSurveyById(@PathVariable Long surveyId) {
        // 이 API는 설문 응답 폼을 보여주기 위한 것 (옵션의 votes는 포함되나, 결과 분석용은 아님)
        SurveyDto.Response responseDto = surveyService.getSurveyById(surveyId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyDto.Response> getSurveyResults(@PathVariable Long surveyId) {
        // 이 API는 설문 결과를 보여주기 위한 것 (옵션의 votes, 전체 응답자 수 등이 중요)
        SurveyDto.Response responseDto = surveyService.getSurveyResultsById(surveyId);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{surveyId}")
    public ResponseEntity<SurveyDto.Response> updateSurvey(
            @PathVariable Long surveyId,
            @RequestBody SurveyDto.Update updateDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SurveyDto.Response responseDto = surveyService.updateSurvey(surveyId, updateDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{surveyId}")
    public ResponseEntity<Void> deleteSurvey(
            @PathVariable Long surveyId,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        surveyService.deleteSurvey(surveyId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<SurveyDto.Response> submitSurveyResponse(
            @PathVariable Long surveyId,
            @RequestBody SurveyDto.SubmitRequest submitRequest,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SurveyDto.Response updatedSurvey = surveyService.submitSurveyResponse(surveyId, submitRequest, userId);
        return ResponseEntity.ok(updatedSurvey); // 업데이트된 설문 정보(votes 포함) 반환
    }
}