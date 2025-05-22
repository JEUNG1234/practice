package com.kh.jpa.dto;

import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Reply;
import com.kh.jpa.enums.CommonEnums; // CommonEnums import
import lombok.*;
import java.time.LocalDateTime;

public class ReplyDto {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Create { // 댓글 작성 요청
        // private Long boardId; // Controller에서 @PathVariable로 받을 예정
        private String content;
        // private String userId; // 작성자 ID는 Controller에서 인증 정보 통해 설정

        public Reply toEntity(Board board, Member member) {
            return Reply.builder()
                    .replyContent(this.content)
                    .board(board)
                    .member(member)
                    .status(CommonEnums.Status.Y)
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Response { // 댓글 조회 응답
        private Long id; // replyNo
        private String content;
        private String authorName; // 작성자 이름
        private String authorId;   // 작성자 ID (수정/삭제 권한 체크용)
        private LocalDateTime createdAt;
        private Long boardId; // 어떤 게시글의 댓글인지 식별용

        public static Response fromEntity(Reply reply) {
            return Response.builder()
                    .id(reply.getReplyNo())
                    .content(reply.getReplyContent())
                    .authorName(reply.getMember() != null ? reply.getMember().getUserName() : "알 수 없음")
                    .authorId(reply.getMember() != null ? reply.getMember().getUserId() : null)
                    .createdAt(reply.getCreateDate())
                    .boardId(reply.getBoard() != null ? reply.getBoard().getBoardNo() : null)
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Update { // 댓글 수정 요청
        private String content;
    }
}