package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Notice;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String noticeTitle;
        private String noticeContent;
        private String userId; // 작성자 ID

        public Notice toEntity(Member writer) {
            return Notice.builder()
                    .noticeTitle(this.noticeTitle)
                    .noticeContent(this.noticeContent)
                    .member(writer) // 연관된 Member 엔티티 설정
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long noticeNo;
        private String noticeTitle;
        private String noticeContent;
        private LocalDateTime createDate;
        private String writerUserId;
        private String writerUserName;

        public static Response fromEntity(Notice notice) {
            return Response.builder()
                    .noticeNo(notice.getNoticeNo())
                    .noticeTitle(notice.getNoticeTitle())
                    .noticeContent(notice.getNoticeContent())
                    .createDate(notice.getCreateDate())
                    .writerUserId(notice.getMember() != null ? notice.getMember().getUserId() : null)
                    .writerUserName(notice.getMember() != null ? notice.getMember().getUserName() : null)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String noticeTitle;
        private String noticeContent;
    }
}