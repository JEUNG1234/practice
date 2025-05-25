package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SURVEY_OPTION")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SurveyOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB 자동 생성 ID
    @Column(name = "OPTION_DB_ID")
    private Long optionDbId;

    @Column(name = "OPT_ID", nullable = false, unique = true, length=36) // 프론트에서 생성한 UUID
    private String optId;

    @Column(name = "OPTION_TEXT", nullable = false, length = 255)
    private String text;

    @Column(name = "VOTES", nullable = false)
    @Builder.Default
    private Integer votes = 0;

    // @Column(name = "OPTION_ORDER") // 옵션 순서 필드 (필요시)
    // private Integer optionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_DB_ID", nullable = false)
    private SurveyQuestion surveyQuestion;
}