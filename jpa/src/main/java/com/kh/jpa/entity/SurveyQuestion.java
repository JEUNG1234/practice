package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SURVEY_QUESTION")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SurveyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_DB_ID")
    private Long questionDbId;

    @Column(name = "Q_ID", nullable = false, unique = true, length=36)
    private String qId; // Lombok이 getQId() 또는 getqId()를 생성 (표준은 getQId)

    @Column(name = "Q_TEXT", nullable = false, columnDefinition = "TEXT")
    private String qText; // Lombok이 getQText() 또는 getqText()를 생성

    @Column(name = "Q_TYPE", nullable = false, length = 50)
    private String qType; // Lombok이 getQType() 또는 getqType()를 생성

    @Column(name = "IS_REQUIRED", nullable = false)
    @Builder.Default
    private boolean isRequired = false; // Lombok이 isRequired()를 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SURVEY_ID", nullable = false)
    private Survey survey;

    @OneToMany(mappedBy = "surveyQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("optionDbId ASC")
    @BatchSize(size = 10)
    private List<SurveyOption> options = new ArrayList<>();

    // 연관관계 편의 메소드
    public void addOption(SurveyOption option) {
        options.add(option);
        option.setSurveyQuestion(this);
    }

    public void removeOption(SurveyOption option){
        options.remove(option);
        option.setSurveyQuestion(null);
    }

    public void clearOptions() {
        this.options.forEach(opt -> opt.setSurveyQuestion(null));
        this.options.clear();
    }
}