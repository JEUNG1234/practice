package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 게시글-태그 매핑 정보를 저장하는 엔티티 클래스
 * Board와 Tag 엔티티 간의 N:M 관계를 매핑
 * 복합키를 사용하여 매핑 정보 관리
 */
@Entity
@Table(name = "BOARD_TAG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardTag {
    @Id
    @Column(name = "BOARD_TAG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardTagId;

    //게시글 : 중계테이블 (1 :  N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_NO", nullable = false)
    private Board board;

    public void changeBoard(Board board) {
        this.board = board;
        board.getBoardTags().add(this);
    }

    //태그 : 중계테이블 (1 :  N)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID", nullable = false)
    private Tag tag;
}