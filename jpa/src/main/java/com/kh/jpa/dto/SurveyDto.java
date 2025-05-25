package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Survey;
import com.kh.jpa.entity.SurveyOption;
import com.kh.jpa.entity.SurveyQuestion;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OptionCreate {
        private String optId;
        private String text;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionCreate {
        private String qId;
        private String qText;
        private String qType;
        private boolean isRequired;
        private List<OptionCreate> options;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        private String title;
        private String description;
        private List<QuestionCreate> questions;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OptionResponse {
        private Long optionDbId;
        private String optId;
        private String text;
        private Integer votes;

        public static OptionResponse fromEntity(SurveyOption option) {
            if (option == null) return null;
            return OptionResponse.builder()
                    .optionDbId(option.getOptionDbId())
                    .optId(option.getOptId())
                    .text(option.getText())
                    .votes(option.getVotes())
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionResponse {
        private Long questionDbId;
        private String qId;
        private String qText;
        private String qType;
        private boolean isRequired;
        private List<OptionResponse> options;

        public static QuestionResponse fromEntity(SurveyQuestion question) {
            if (question == null) return null;
            return QuestionResponse.builder()
                    .questionDbId(question.getQuestionDbId())
                    // Lombok이 SurveyQuestion의 필드명에 따라 생성하는 getter 호출로 수정
                    .qId(question.getQId())      // 수정: getqId() -> getQId()
                    .qText(question.getQText())  // 수정: getqText() -> getQText()
                    .qType(question.getQType())  // 수정: getqType() -> getQType()
                    .isRequired(question.isRequired()) // isRequired()는 boolean 타입의 표준 getter
                    .options(question.getOptions() != null ?
                            question.getOptions().stream()
                                    .map(OptionResponse::fromEntity)
                                    .collect(Collectors.toList()) : List.of())
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private String authorName;
        private String authorId;
        private LocalDateTime createdAt;
        private String status;
        private Integer totalRespondents;
        private List<QuestionResponse> questions;

        public static Response fromEntity(Survey survey) {
            if (survey == null) return null;
            return Response.builder()
                    .id(survey.getId())
                    .title(survey.getTitle())
                    .description(survey.getDescription())
                    .authorName(survey.getMember() != null ? survey.getMember().getUserName() : "알 수 없음")
                    .authorId(survey.getMember() != null ? survey.getMember().getUserId() : null)
                    .createdAt(survey.getCreatedAt())
                    .status(survey.getStatus())
                    .totalRespondents(survey.getTotalRespondents())
                    .questions(survey.getQuestions() != null ?
                            survey.getQuestions().stream()
                                    .map(QuestionResponse::fromEntity)
                                    .collect(Collectors.toList()) : List.of())
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        private String title;
        private String description;
        private List<QuestionCreate> questions;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubmittedQuestionResponse {
        private String qId;
        private List<String> selectedOptIds;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SubmitRequest {
        private List<SubmittedQuestionResponse> responses;
    }
}