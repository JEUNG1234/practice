package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POLL")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLL_ID")
    private Long id;

    @Column(name = "POLL_TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "POLL_TYPE", nullable = false, length = 50) // e.g., "singleChoice", "multipleChoice"
    private String pollType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLL_AUTHOR_USER_ID", nullable = false)
    private Member member; // 작성자

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "TOTAL_VOTES", nullable = false)
    @Builder.Default
    private Integer totalVotes = 0;

    // mappedBy는 PollOption 엔티티의 poll 필드명
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @BatchSize(size = 10) // N+1 문제 완화를 위해 추가
    private List<PollOption> options = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.totalVotes == null) {
            this.totalVotes = 0;
        }
    }

    // 연관관계 편의 메소드
    public void addOption(PollOption option) {
        options.add(option);
        option.setPoll(this);
    }

    public void removeOption(PollOption option) {
        options.remove(option);
        option.setPoll(null);
    }

    public void clearOptions() {
        this.options.forEach(option -> option.setPoll(null));
        this.options.clear();
    }
}