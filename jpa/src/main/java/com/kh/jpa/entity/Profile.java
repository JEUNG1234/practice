package com.kh.jpa.entity;

import jakarta.persistence.*;
import lombok.*; // Getter, Setter 추가

@Getter // Lombok 추가
@Setter // Lombok 추가 (양방향 연관관계 편의 메서드 위해)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFILE_ID")
    private Long profileId;

    @Column(name = "PROFILE_IMAGE", length = 100)
    private String profileImage;

    @Column(length = 300)
    private String intro;

    // Member 엔티티의 'profile' 필드에 의해 매핑됨
    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private Member member;

    // 연관관계 편의 메서드 (양방향 설정 시)
    public void setMember(Member member) {
        this.member = member;
    }
}