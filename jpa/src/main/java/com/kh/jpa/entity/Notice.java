package com.kh.jpa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_NO")
    private Long noticeNo;

    @Column(name = "NOTICE_TITLE", length = 30, nullable = false)
    private String noticeTitle;

    @Column(name = "NOTICE_CONTENT", length = 200, nullable = false)
    private String noticeContent;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NOTICE_WRITER")
    private Member member;

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
    }


    public void updateDetails(String newTitle, String newContent) {
        if (newTitle != null && !newTitle.isEmpty()) {
            this.noticeTitle = newTitle;
        }
        if (newContent != null && !newContent.isEmpty()) {
            this.noticeContent = newContent;
        }
    }
}