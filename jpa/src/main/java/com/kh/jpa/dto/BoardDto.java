package com.kh.jpa.dto;

import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member; // Member 참조 위해 import
import lombok.*;
import java.time.LocalDateTime;
// import org.springframework.web.multipart.MultipartFile; // 파일 제외
// import java.util.List; // 태그 제외

public class BoardDto {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Create { // BoardCreate.jsx
        private String title;  // board_title
        private String body;   // board_content
        // private String userId; // 작성자 ID는 Controller에서 인증 정보 통해 설정

        public Board toEntity(Member member) { // 작성자 Member 엔티티를 받도록 수정
            return Board.builder()
                    .boardTitle(this.title)
                    .boardContent(this.body)
                    .member(member) // 작성자 설정
                    .status(com.kh.jpa.enums.CommonEnums.Status.Y)
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Response { // BoardList.jsx, BoardDetail.jsx 응답용
        private Long id; // boardNo
        private String title;
        private String body; // 상세보기에선 필요, 목록에선 선택적
        private String authorName; // 작성자 이름 (member.userName)
        private String authorId; // 작성자 ID (member.userId) - 수정/삭제 권한 체크용
        private LocalDateTime createdAt; // createDate
        private Integer viewCount; // count

        // private String originName; // 파일 제외
        // private String changeName; // 파일 제외
        // private List<String> tags; // 태그 제외

        public static Response fromEntity(Board board) {
            return Response.builder()
                    .id(board.getBoardNo())
                    .title(board.getBoardTitle())
                    .body(board.getBoardContent()) // 상세 조회 시 본문 포함
                    .authorName(board.getMember() != null ? board.getMember().getUserName() : "알 수 없음")
                    .authorId(board.getMember() != null ? board.getMember().getUserId() : null)
                    .createdAt(board.getCreateDate())
                    .viewCount(board.getViewCount())
                    .build();
        }

        // 목록 조회를 위한 간소화된 DTO (본문 제외 등)
        public static Response forList(Board board) {
            return Response.builder()
                    .id(board.getBoardNo())
                    .title(board.getBoardTitle())
                    .authorName(board.getMember() != null ? board.getMember().getUserName() : "알 수 없음")
                    .createdAt(board.getCreateDate())
                    .viewCount(board.getViewCount())
                    .build();
        }
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Update { // BoardEdit.jsx
        private String title;
        private String body;
        // private String userId; // 수정 권한 확인은 Controller에서
        // private MultipartFile file; // 파일 제외
        // private List<String> tags; // 태그 제외
    }
}