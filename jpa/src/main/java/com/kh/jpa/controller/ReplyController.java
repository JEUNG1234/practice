package com.kh.jpa.controller;

import com.kh.jpa.dto.ReplyDto;
import com.kh.jpa.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin // CORS 설정
@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/api/boards/{boardId}/replies")
    public ResponseEntity<ReplyDto.Response> createReply(
            @PathVariable Long boardId,
            @RequestBody ReplyDto.Create createDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReplyDto.Response responseDto = replyService.createReply(boardId, createDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/api/boards/{boardId}/replies")
    public ResponseEntity<List<ReplyDto.Response>> getRepliesByBoard(@PathVariable Long boardId) {
        List<ReplyDto.Response> replies = replyService.getRepliesByBoardId(boardId);
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/api/replies/{replyId}")
    public ResponseEntity<ReplyDto.Response> updateReply(
            @PathVariable Long replyId,
            @RequestBody ReplyDto.Update updateDto,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ReplyDto.Response responseDto = replyService.updateReply(replyId, updateDto, userId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/api/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long replyId,
            @RequestHeader(value = "X-USER-ID") String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        replyService.deleteReply(replyId, userId);
        return ResponseEntity.noContent().build();
    }
}