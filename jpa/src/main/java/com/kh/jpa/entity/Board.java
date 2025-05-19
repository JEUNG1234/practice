package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA 스펙상 필수 + 외부 생성 방지
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_NO")
    private Long boardNo;

    @Column(name = "BOARD_TITLE", length = 30, nullable = false)
    private String boardTitle;

    //@Lob : 대용량 데이터 매핑
    @Column(name = "BOARD_CONTENT", nullable = false)
    @Lob
    private String noticeContent;

    @Column(name = "ORIGIN_NAME", length = 100)
    private String originName;

    @Column(name = "CHANGE_NAME", length = 100)
    private String changeName;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.Status status;

    private Integer count;

    //Board : Member( N : 1 )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_WRITER")
    private Member member;

    //Reply : Board (N : 1)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    //BoardTag : Board (N : 1)
    @OneToMany(mappedBy = "board")
    private List<BoardTag> boardTags = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDateTime.now();
        this.count = 0;
        if(this.status == null) {
            this.status = CommonEnums.Status.Y;
        }
    }
}