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
@Setter
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
    private String userId; // email 형식 ID

    @Column(name = "USER_PWD", length = 100, nullable = false)
    private String userPwd;

    @Column(name = "USER_NAME", length = 50, nullable = false)
    private String userName;

    @Column(name = "EMAIL", length = 254, unique = true) // 실제 이메일 (userId와 같을 수 있음)
    private String email;

    // Mypage.jsx 에서 성별, 전화번호, 주소, 나이 필드가 없으므로 우선 주석처리 또는 선택적 추가
    // @Column(name = "GENDER", length = 1)
    // @Enumerated(EnumType.STRING)
    // private Gender gender;

    // @Column(name = "PHONE", length = 20)
    // private String phone;

    // @Column(name = "ADDRESS", length = 255)
    // private String address;

    // @Column(name = "AGE")
    // private Integer age;

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


    // public enum Gender { M, F } // 필요시 사용

    // Mypage.jsx 에서는 주로 비밀번호 변경이 핵심
    public void updateUserInfo(String userName /*, String phone, String address, Integer age, Gender gender*/) {
        if (userName != null && !userName.isEmpty()) { // Mypage.jsx에서는 이름 변경 UI 없음
            this.userName = userName;
        }
        // 나머지 필드 업데이트 로직 (필요시)
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