package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "REPLY") // 테이블명 명시
@Getter
@Setter // 내용 수정 등을 위해 Setter 추가 또는 변경 메소드 구현
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPLY_NO")
    private Long replyNo;

    @Column(name = "REPLY_CONTENT", length = 1000, nullable = false) // 댓글 길이 고려
    private String replyContent;

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "STATUS", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_NO") // FK 컬럼명 BOARD_NO로 통일 (Board 엔티티의 PK 컬럼명 참조)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPLY_WRITER_USER_ID") // Member의 userId와 연결
    private Member member;

    // 연관관계 편의 메소드
    public void setBoard(Board board) {
        this.board = board;
        if (board != null && !board.getReplies().contains(this)) {
            board.getReplies().add(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (member != null && !member.getReplies().contains(this)) {
            member.getReplies().add(this);
        }
    }

    public void updateContent(String content) {
        if (content != null && !content.isEmpty()) {
            this.replyContent = content;
        }
    }

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = CommonEnums.Status.Y;
        }
    }
}