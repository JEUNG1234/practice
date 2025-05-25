package com.kh.jpa.controller;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.PollDto;
import com.kh.jpa.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    // 새 투표 생성
    @PostMapping
    public ResponseEntity<PollDto.Response> createPoll(
            @RequestBody PollDto.Create createDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PollDto.Response responseDto = pollService.createPoll(createDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 모든 투표 목록 조회 (페이징) 또는 HOT 투표 조회
    // GET /api/polls?hot=true (HOT 투표 3개)
    // GET /api/polls (전체 투표, 페이징)
    @GetMapping
    public ResponseEntity<PageResponse<PollDto.Response>> getPolls(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "hot", required = false) boolean hot) {
        if (hot) {
            // HOT 투표는 PollList.jsx 및 MainDashboardPage.jsx에서 3개만 가져옴
            Pageable hotPageable = PageRequest.of(0, 3, Sort.by("totalVotes").descending().and(Sort.by("createdAt").descending()));
            return ResponseEntity.ok(pollService.getHotPolls(hotPageable));
        }
        return ResponseEntity.ok(pollService.getAllPolls(pageable));
    }

    // 특정 투표 상세 조회
    @GetMapping("/{pollId}")
    public ResponseEntity<PollDto.Response> getPollById(@PathVariable Long pollId) {
        PollDto.Response responseDto = pollService.getPollById(pollId);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 투표 수정
    @PutMapping("/{pollId}")
    public ResponseEntity<PollDto.Response> updatePoll(
            @PathVariable Long pollId,
            @RequestBody PollDto.Update updateDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PollDto.Response responseDto = pollService.updatePoll(pollId, updateDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 투표 삭제
    @DeleteMapping("/{pollId}")
    public ResponseEntity<Void> deletePoll(
            @PathVariable Long pollId,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        pollService.deletePoll(pollId, userId);
        return ResponseEntity.noContent().build();
    }

    // 특정 투표에 투표하기
    @PostMapping("/{pollId}/vote")
    public ResponseEntity<PollDto.Response> voteOnPoll(
            @PathVariable Long pollId,
            @RequestBody PollDto.VoteRequest voteRequest,
            @RequestHeader(value = "X-USER-ID", required = false) String userId // 비로그인 투표 허용 시 false, 아니면 true
    ) {
        // 프론트 PollDetail.jsx에서 로그인 안 한 사용자는 투표 못하게 막고 있음.
        // 백엔드에서도 userId가 없으면 401 반환.
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        PollDto.Response responseDto = pollService.voteOnPoll(pollId, voteRequest, userId);
        return ResponseEntity.ok(responseDto);
    }
}