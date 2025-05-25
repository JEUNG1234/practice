package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SURVEY")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SURVEY_ID")
    private Long id;

    @Column(name = "SURVEY_TITLE", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "SURVEY_DESCRIPTION")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SURVEY_AUTHOR_USER_ID", nullable = false)
    private Member member; // 작성자

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "STATUS", length = 50) // e.g., "published", "draft", "closed"
    private String status;

    @Column(name = "TOTAL_RESPONDENTS", nullable = false)
    @Builder.Default
    private Integer totalRespondents = 0;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("questionOrder ASC") // 질문 순서 (별도 필드 추가 시) 또는 ID 순
    @BatchSize(size = 20)
    private List<SurveyQuestion> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalRespondents == null) {
            this.totalRespondents = 0;
        }
        if (this.status == null) {
            this.status = "published"; // 기본 상태
        }
    }

    // 연관관계 편의 메소드
    public void addQuestion(SurveyQuestion question) {
        questions.add(question);
        question.setSurvey(this);
    }

    public void removeQuestion(SurveyQuestion question) {
        questions.remove(question);
        question.setSurvey(null);
    }

    public void clearQuestions() {
        this.questions.forEach(q -> q.setSurvey(null));
        this.questions.clear();
    }
}