package com.kh.board.controller.dto.request;

import com.kh.board.entity.Board;
import com.kh.board.entity.Member;
import lombok.Getter;
import lombok.Setter;

public class BoardRequest {

    @Getter
    @Setter
    public static class CreateDTO {

        private String user_id;
        private String title;
        private String contents;
        private String file_name;

        public Board toEntity(Member member) {
            return Board.builder()
                        .title(title)
                        .member(member)
                        .contents(contents)
                        .fileName(file_name)
                        .build();
        }
    }

    @Getter
    @Setter
    public static class UpdateDTO {

        private Long boardId;
        private String user_id;
        private String title;
        private String contents;
        private String origin_file;

        public Board toEntity(Member member) {
            return Board.builder()
                        .member(member)
                        .boardId(boardId)
                        .title(title)
                        .contents(contents)
                        .fileName(origin_file)
                        .build();
        }
    }
}
