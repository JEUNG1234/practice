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
@AllArgsConstructor
@Builder
public class BoardTag {

    @Id
    @Column(name = "BOARD_NO")
    private Long boardNo;

    @Id
    @Column(name = "TAG_ID")
    private Long tagId;

    @ManyToOne
    @JoinColumn(name = "BOARD_NO", insertable = false, updatable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "TAG_ID", insertable = false, updatable = false)
    private Tag tag;

    // 생성자, Getter, Setter
    public BoardTag() {}

    public BoardTag(Long boardNo, Long tagId) {
        this.boardNo = boardNo;
        this.tagId = tagId;
    }

    public Long getBoardNo() {
        return boardNo;
    }

    public void setBoardNo(Long boardNo) {
        this.boardNo = boardNo;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}