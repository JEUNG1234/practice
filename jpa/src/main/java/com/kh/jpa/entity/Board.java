package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "BOARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_NO")
    private Long boardNo;

    @Column(name = "BOARD_TITLE", length = 100, nullable = false) // 프론트 title 필드 고려하여 길이 조정
    private String boardTitle;

    @Lob // 본문 내용이 길 수 있으므로 @Lob 사용
    @Column(name = "BOARD_CONTENT", nullable = false)
    private String boardContent;

    // 파일 업로드 기능이 프론트엔드에 명확히 없으므로 우선 주석 처리
    // @Column(name = "ORIGIN_NAME", length = 255)
    // private String originName;

    // @Column(name = "CHANGE_NAME", length = 255)
    // private String changeName;

    @Column(name = "CREATE_DATE", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "STATUS", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.Status status;

    @Column(name = "VIEW_COUNT") // 조회수 필드 (BoardDto.Response 에 count로 있음)
    private Integer viewCount; // 필드명 통일 (count -> viewCount)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_WRITER_USER_ID") // Member의 userId와 연결
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    // 연관관계 편의 메소드
    public void setMember(Member member) {
        this.member = member;
        if (member != null && !member.getBoards().contains(this)) {
            member.getBoards().add(this);
        }
    }

    // 파일 관련 메소드 (주석 처리)
    // public void changeFile(String originName, String changeName) {
    //     this.originName = originName;
    //     this.changeName = changeName;
    // }

    public void updateBoard(String title, String content) {
        if (title != null && !title.isEmpty()) {
            this.boardTitle = title;
        }
        if (content != null && !content.isEmpty()) {
            this.boardContent = content;
        }
    }

    public void incrementViewCount() {
        if (this.viewCount == null) {
            this.viewCount = 0;
        }
        this.viewCount++;
    }


    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        if (this.viewCount == null) this.viewCount = 0;
        if (this.status == null) {
            this.status = CommonEnums.Status.Y;
        }
    }
}