package com.kh.board.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "MEMBER")
@Getter
public class Member {

    @Id
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100) //최대길이 100자
    private String nickname;

    @CreationTimestamp //엔티티가 처음저장될 때 자동으로 현재 시간 저장
    @Column(name = "created_at", updatable = false) //최초생성시간은 변경이 불가하도록
    private LocalDateTime createdAt;

    @UpdateTimestamp //엔티티가 수정될대마다 자동으로 업데이트 시간을 넣어줌
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    //Member : Board = 1 : N 관계 (한명의 회원은 여러 게시글을 작성할 수 있음)
    // mappedBy = "member" -> Board 엔티티의 'member' 필드가 외래키 주인임을 의미
    // cascade = CascadeType.ALL -> 회원 삭제시 관련 게시글 모두 삭제
    private List<Board> boards = new ArrayList<>();
}
