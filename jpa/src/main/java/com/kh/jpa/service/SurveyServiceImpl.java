package com.kh.jpa.service;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.SurveyDto;
import com.kh.jpa.entity.*;
import com.kh.jpa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyOptionRepository surveyOptionRepository;
    private final MemberRepository memberRepository;

    // ... (createSurvey, getAllSurveys, getHotSurveys, getSurveyById, getSurveyResultsById, updateSurvey, deleteSurvey 메서드는 이전과 동일) ...

    @Override
    public SurveyDto.Response createSurvey(SurveyDto.Create createDto, String userId) {
        Member author = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다. ID: " + userId));

        Survey survey = Survey.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .member(author)
                .build();

        List<SurveyQuestion> questions = new ArrayList<>();
        if (createDto.getQuestions() != null) {
            for (SurveyDto.QuestionCreate qDto : createDto.getQuestions()) {
                SurveyQuestion question = SurveyQuestion.builder()
                        .qId(qDto.getQId())
                        .qText(qDto.getQText())
                        .qType(qDto.getQType())
                        .isRequired(qDto.isRequired())
                        .survey(survey)
                        .build();
                if (qDto.getOptions() != null && ("singleChoice".equals(qDto.getQType()) || "multipleChoice".equals(qDto.getQType()))) {
                    List<SurveyOption> options = qDto.getOptions().stream()
                            .map(optDto -> SurveyOption.builder()
                                    .optId(optDto.getOptId())
                                    .text(optDto.getText())
                                    .surveyQuestion(question)
                                    .votes(0)
                                    .build())
                            .collect(Collectors.toList());
                    question.setOptions(options);
                }
                questions.add(question);
            }
        }
        survey.setQuestions(questions);

        Survey savedSurvey = surveyRepository.save(survey);
        return SurveyDto.Response.fromEntity(savedSurvey);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SurveyDto.Response> getAllSurveys(Pageable pageable) {
        Page<Survey> surveyPage = surveyRepository.findAllByOrderByCreatedAtDesc(pageable);
        return new PageResponse<>(surveyPage.map(SurveyDto.Response::fromEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SurveyDto.Response> getHotSurveys(Pageable pageable) {
        Pageable topThree = PageRequest.of(0, 3, Sort.by("totalRespondents").descending().and(Sort.by("createdAt").descending()));
        Page<Survey> surveyPage = surveyRepository.findAllByOrderByTotalRespondentsDescCreatedAtDesc(topThree);
        return new PageResponse<>(surveyPage.map(SurveyDto.Response::fromEntity));
    }


    @Override
    @Transactional(readOnly = true)
    public SurveyDto.Response getSurveyById(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("설문을 찾을 수 없습니다. ID: " + surveyId));
        return SurveyDto.Response.fromEntity(survey);
    }

    @Override
    @Transactional(readOnly = true)
    public SurveyDto.Response getSurveyResultsById(Long surveyId) {
        return getSurveyById(surveyId);
    }

    @Override
    public SurveyDto.Response updateSurvey(Long surveyId, SurveyDto.Update updateDto, String userId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("수정할 설문을 찾을 수 없습니다. ID: " + surveyId));

        if (survey.getMember() == null || !survey.getMember().getUserId().equals(userId)) {
            throw new SecurityException("설문 수정 권한이 없습니다.");
        }

        survey.setTitle(updateDto.getTitle());
        survey.setDescription(updateDto.getDescription());
        survey.clearQuestions();

        if (updateDto.getQuestions() != null) {
            for (SurveyDto.QuestionCreate qDto : updateDto.getQuestions()) {
                SurveyQuestion question = SurveyQuestion.builder()
                        .qId(qDto.getQId())
                        .qText(qDto.getQText())
                        .qType(qDto.getQType())
                        .isRequired(qDto.isRequired())
                        .survey(survey)
                        .build();
                if (qDto.getOptions() != null && ("singleChoice".equals(qDto.getQType()) || "multipleChoice".equals(qDto.getQType()))) {
                    List<SurveyOption> options = qDto.getOptions().stream()
                            .map(optDto -> SurveyOption.builder()
                                    .optId(optDto.getOptId())
                                    .text(optDto.getText())
                                    .surveyQuestion(question)
                                    .votes(0)
                                    .build())
                            .collect(Collectors.toList());
                    question.setOptions(options);
                }
                survey.addQuestion(question);
            }
        }
        Survey updatedSurvey = surveyRepository.save(survey);
        return SurveyDto.Response.fromEntity(updatedSurvey);
    }

    @Override
    public void deleteSurvey(Long surveyId, String userId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 설문을 찾을 수 없습니다. ID: " + surveyId));
        if (survey.getMember() == null || !survey.getMember().getUserId().equals(userId)) {
            throw new SecurityException("설문 삭제 권한이 없습니다.");
        }
        surveyRepository.delete(survey);
    }

    @Override
    public SurveyDto.Response submitSurveyResponse(Long surveyId, SurveyDto.SubmitRequest submitRequest, String userId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new EntityNotFoundException("설문을 찾을 수 없습니다. ID: " + surveyId));

        if (survey.getMember() != null && survey.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신이 만든 설문에는 응답할 수 없습니다.");
        }

        for (SurveyDto.SubmittedQuestionResponse qResponse : submitRequest.getResponses()) {
            SurveyQuestion question = survey.getQuestions().stream()
                    // 수정된 부분: getQId() 사용
                    .filter(q -> q.getQId().equals(qResponse.getQId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다. Q_ID: " + qResponse.getQId()));

            // 수정된 부분: getQType() 사용
            if ("singleChoice".equals(question.getQType()) || "multipleChoice".equals(question.getQType())) {
                if (qResponse.getSelectedOptIds() == null || qResponse.getSelectedOptIds().isEmpty()) {
                    // 수정된 부분: getQText() 사용
                    if (question.isRequired()) {
                        throw new IllegalArgumentException("필수 질문에 답변하지 않았습니다: " + question.getQText());
                    }
                    continue;
                }
                for (String selectedOptId : qResponse.getSelectedOptIds()) {
                    SurveyOption option = question.getOptions().stream()
                            .filter(opt -> opt.getOptId().equals(selectedOptId))
                            .findFirst()
                            .orElseThrow(() -> new EntityNotFoundException("옵션을 찾을 수 없습니다. OPT_ID: " + selectedOptId));
                    option.setVotes(option.getVotes() + 1);
                }
            }
        }

        survey.setTotalRespondents(survey.getTotalRespondents() + 1);
        Survey savedSurvey = surveyRepository.save(survey);
        return SurveyDto.Response.fromEntity(savedSurvey);
    }
}