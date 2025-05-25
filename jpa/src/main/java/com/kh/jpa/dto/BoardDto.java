package com.kh.jpa.dto;

import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import lombok.*;
import java.time.LocalDateTime;

public class BoardDto {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Create {
        private String title;
        private String body;
        // 'author' and 'createdAt' are handled by the backend using authenticated user and @PrePersist

        public Board toEntity(Member member) {
            return Board.builder()
                    .boardTitle(this.title)
                    .boardContent(this.body)
                    .member(member) // Set the authenticated member
                    .status(com.kh.jpa.enums.CommonEnums.Status.Y)
                    .viewCount(0) // Initial view count
                    // createDate will be set by @PrePersist in Board entity
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String title;
        private String body;
        private String authorName;
        private String authorId; // For frontend permission checks (e.g., edit/delete buttons)
        private LocalDateTime createdAt;
        private Integer viewCount;

        public static Response fromEntity(Board board) {
            return Response.builder()
                    .id(board.getBoardNo())
                    .title(board.getBoardTitle())
                    .body(board.getBoardContent())
                    .authorName(board.getMember() != null ? board.getMember().getUserName() : "알 수 없음")
                    .authorId(board.getMember() != null ? board.getMember().getUserId() : null)
                    .createdAt(board.getCreateDate())
                    .viewCount(board.getViewCount())
                    .build();
        }

        // Simplified DTO for lists (e.g., without full body content)
        public static Response forList(Board board) {
            return Response.builder()
                    .id(board.getBoardNo())
                    .title(board.getBoardTitle())
                    .authorName(board.getMember() != null ? board.getMember().getUserName() : "알 수 없음")
                    .authorId(board.getMember() != null ? board.getMember().getUserId() : null)
                    .createdAt(board.getCreateDate())
                    .viewCount(board.getViewCount())
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Update {
        private String title;
        private String body;
        // The 'author' field from the frontend form in BoardEdit.jsx is not used to change the author.
        // It can be used for permission checks if needed, but typically, the author of a post doesn't change.
    }
}