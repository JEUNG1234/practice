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

        // PasswordEncoder를 사용하지 않으므로, 파라미터로 평문 비밀번호를 받거나 DTO의 password 필드를 직접 사용합니다.
        // 여기서는 MemberServiceImpl에서 createDto.getPassword()를 직접 전달하는 것을 가정하고,
        // toEntity 메소드 내부에서 this.password를 사용하도록 변경합니다.
        public Member toEntity() { // 파라미터 제거
            return Member.builder()
                    .userId(this.email)
                    .userPwd(this.password) // DTO의 password 필드를 직접 사용 (평문)
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
        // private String userName; // 이름 변경 필요시 (현재 프론트엔드 Mypage.jsx 에서는 이름 변경 UI 없음)
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