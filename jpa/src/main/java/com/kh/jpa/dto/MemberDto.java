package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import com.kh.jpa.enums.CommonEnums;
import lombok.*;
import java.time.LocalDateTime;

public class MemberDto {

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Create { // Register.jsx
        private String name;
        private String email;    // userId
        private String password;

        public Member toEntity(String encryptedPassword) {
            return Member.builder()
                    .userId(this.email)
                    .userPwd(encryptedPassword)
                    .userName(this.name)
                    .email(this.email) // Member 엔티티의 email 필드에도 저장
                    .status(CommonEnums.Status.Y) // 기본 활성 상태
                    .build();
        }
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class Response { // 프론트엔드 응답용
        private String id;       // userId
        private String name;
        private String email;
        private LocalDateTime enrollDate;
        // Mypage.jsx에서 필요한 추가 정보가 있다면 여기에 포함
        // private Member.Gender gender;
        // private String phone;
        // private String address;
        // private Integer age;
        // private CommonEnums.Status status;

        public static Response fromEntity(Member member) {
            return Response.builder()
                    .id(member.getUserId())
                    .name(member.getUserName())
                    .email(member.getEmail())
                    .enrollDate(member.getEnrollDate())
                    // .gender(member.getGender())
                    // .phone(member.getPhone())
                    // .address(member.getAddress())
                    // .age(member.getAge())
                    // .status(member.getStatus())
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class Update { // Mypage.jsx
        private String currentPassword;
        private String newPassword;
        // private String userName; // 이름 변경 필요시
        // private String phone;
        // private String address;
        // private Integer age;
        // private Member.Gender gender;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class LoginRequest { // Login.jsx
        private String email;
        private String password;
    }

    @Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
    public static class LoginResponse {
        private String token;
        private Response userInfo;
    }
}