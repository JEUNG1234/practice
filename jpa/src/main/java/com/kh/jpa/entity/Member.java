package com.kh.jpa.entity;

import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter // 양방향 연관관계 편의 메서드 등을 위해 Setter 허용
@Entity
@Table(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Member {

    @Id
    @Column(name = "USER_ID", length = 254)
    private String userId;

    @Column(name = "USER_PWD", length = 100, nullable = false)
    private String userPwd;

    @Column(name = "USER_NAME", length = 50, nullable = false)
    private String userName;

    @Column(name = "EMAIL", length = 254, unique = true)
    private String email;

    @Column(name = "ENROLL_DATE", updatable = false)
    private LocalDateTime enrollDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;

    @Column(name = "STATUS", length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.Status status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    // Profile과의 일대일 양방향 연관관계 추가
    // Member가 Profile의 주인이 됩니다. Profile 저장 시 Member에 Profile을 설정하고 저장하면 됩니다.
    // 또는 Profile이 저장될 때 Member의 profile 필드도 함께 업데이트 되도록 cascade 설정.
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID") // Member 테이블에 PROFILE_ID 외래 키 생성
    private Profile profile; // Profile 엔티티의 mappedBy 값과 일치해야 함

    // 연관관계 편의 메서드 (양방향 설정 시)
    public void setProfile(Profile profile) {
        this.profile = profile;
        if (profile != null && profile.getMember() != this) {
            profile.setMember(this); // Profile 엔티티에도 Member 설정 (양방향일 경우)
        }
    }


    public void updateUserInfo(String userName) {
        if (userName != null && !userName.isEmpty()) {
            this.userName = userName;
        }
    }

    public void changePassword(String newPassword) {
        this.userPwd = newPassword;
    }

    @PrePersist
    protected void onCreate() {
        this.enrollDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = CommonEnums.Status.Y;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = LocalDateTime.now();
    }
}