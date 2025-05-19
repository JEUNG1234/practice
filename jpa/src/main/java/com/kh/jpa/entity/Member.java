package com.kh.jpa.entity;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.enums.CommonEnums;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA 스펙상 필수 + 외부 생성 방지
@AllArgsConstructor
@Builder
@DynamicInsert// insert시에 null이 아닌 필드만 쿼리에 포함, default값 활용
@DynamicUpdate// 변경된 필드만 update문에 포함
public class Member {

    @Id
    @Column(name = "USER_ID", length = 30)
    private String userId;

    @Column(name = "USER_PWD", length = 100, nullable = false)
    private String userPwd;

    @Column(name = "USER_NAME", length = 15, nullable = false)
    private String userName;

    @Column(length = 254)
    private String email;

    @Column(name = "GENDER", length = 1)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 13)
    private String phone;

    @Column(length = 100)
    private String address;

    @Column(name = "ENROLL_DATE")
    private LocalDateTime enrollDate;

    @Column(name = "MODIFY_DATE")
    private LocalDateTime modifyDate;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    private CommonEnums.Status status;

    private Integer age;

    //1 : N 연관관계 주인 = Board
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<Board> boards = new ArrayList<>();

    //1 : N 연관관계 주인 = Notice
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<Notice> notices = new ArrayList<>();

    //회원 : 프로필 (1 : 1)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PROFILE_ID", unique = true)
    private Profile profile;


    public enum Gender {
        M, F
    }

    public void updateMemberInfo(String username, String email, Gender gender, String phone, String address, Integer age) {
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.age = age;

    }

    @PrePersist
    public void prePersist() {
        this.enrollDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
        if(this.status == null) {
            this.status = CommonEnums.Status.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modifyDate = LocalDateTime.now();
    }
}