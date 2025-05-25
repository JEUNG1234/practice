package com.kh.jpa.dto;

import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Poll;
import com.kh.jpa.entity.PollOption;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PollDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateOption { // 옵션 생성 시 DTO
        private String text;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Create {
        private String title;
        private String pollType; // "singleChoice" or "multipleChoice"
        private List<CreateOption> options; // 옵션 텍스트 목록

        public Poll toEntity(Member author, List<PollOption> pollOptions) {
            Poll poll = Poll.builder()
                    .title(this.title)
                    .pollType(this.pollType)
                    .member(author)
                    .totalVotes(0)
                    // createdAt은 @PrePersist로 자동 생성
                    .build();
            // 옵션들은 서비스 레이어에서 Poll 객체에 설정
            return poll;
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class OptionResponse {
        private Long id; // PollOption의 DB ID
        private String text;
        private Integer votes;

        public static OptionResponse fromEntity(PollOption option) {
            return OptionResponse.builder()
                    .id(option.getId())
                    .text(option.getText())
                    .votes(option.getVotes())
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String title;
        private String pollType;
        private String authorName;
        private String authorId; // 수정/삭제 권한 확인용
        private LocalDateTime createdAt;
        private Integer totalVotes;
        private List<OptionResponse> options;

        public static Response fromEntity(Poll poll) {
            return Response.builder()
                    .id(poll.getId())
                    .title(poll.getTitle())
                    .pollType(poll.getPollType())
                    .authorName(poll.getMember() != null ? poll.getMember().getUserName() : "알 수 없음")
                    .authorId(poll.getMember() != null ? poll.getMember().getUserId() : null)
                    .createdAt(poll.getCreatedAt())
                    .totalVotes(poll.getTotalVotes())
                    .options(poll.getOptions().stream()
                            .map(OptionResponse::fromEntity)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Update {
        private String title;
        private String pollType;
        private List<CreateOption> options; // 옵션 수정/추가/삭제를 위해 옵션 텍스트 목록 전달
        // 기존 옵션 ID를 함께 보내거나, 서비스 로직에서 기존 옵션 삭제 후 재생성 방식 사용 가능
        // 여기서는 단순화를 위해 전체 옵션을 새로 받는다고 가정 (votes는 유지 필요)
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class VoteRequest {
        private List<Long> selectedOptionIds; // 선택된 PollOption의 DB ID 목록
    }
}