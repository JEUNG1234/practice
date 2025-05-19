package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import com.kh.jpa.enums.CommonEnums;
import lombok.*;

import java.time.LocalDateTime;

public class MemberDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Create {

        private String user_id;
        private String user_pwd;
        private String user_name;
        private String email;
        private Member.Gender gender;
        private String phone;
        private String address;
        private Integer age;

        public Member toEntity() {
            return Member.builder()
                    .userId(this.user_id)
                    .userPwd(this.user_pwd)
                    .userName(this.user_name)
                    .email(this.email)
                    .gender(this.gender)
                    .age(this.age)
                    .phone(this.phone)
                    .address(this.address)
                    .build();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private String user_id;
        private String user_pwd;
        private String user_name;
        private String email;
        private Member.Gender gender;
        private String phone;
        private String address;
        private Integer age;
        private LocalDateTime modifyDate;
        private LocalDateTime enrollDate;
        private CommonEnums.Status status;

        public static Response toDto(Member member) {
            return Response.builder()
                    .user_id(member.getUserId())
                    .user_name(member.getUserName())
                    .email(member.getEmail())
                    .gender(member.getGender())
                    .age(member.getAge())
                    .phone(member.getPhone())
                    .address(member.getAddress())
                    .enrollDate(member.getEnrollDate())
                    .modifyDate(member.getModifyDate())
                    .status(member.getStatus())
                    .build();
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update {

        private String user_name;
        private String email;
        private Member.Gender gender;
        private String phone;
        private String address;
        private Integer age;



    }
}