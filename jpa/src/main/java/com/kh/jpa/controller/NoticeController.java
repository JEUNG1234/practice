package com.kh.jpa.controller;

import com.kh.jpa.dto.NoticeDto;
import com.kh.jpa.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<NoticeDto.Response> createNotice(@RequestBody NoticeDto.Create createDto) {
        NoticeDto.Response responseDto = noticeService.createNotice(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 특정 공지사항 조회
    @GetMapping("/{noticeNo}")
    public ResponseEntity<NoticeDto.Response> getNotice(@PathVariable Long noticeNo) {
        NoticeDto.Response responseDto = noticeService.getNoticeById(noticeNo);
        return ResponseEntity.ok(responseDto);
    }

    // 모든 공지사항 조회
    @GetMapping
    public ResponseEntity<List<NoticeDto.Response>> getAllNotices() {
        List<NoticeDto.Response> responseDtos = noticeService.getAllNotices();
        return ResponseEntity.ok(responseDtos);
    }

    // 공지사항 수정
    @PutMapping("/{noticeNo}")
    public ResponseEntity<NoticeDto.Response> updateNotice(@PathVariable Long noticeNo, @RequestBody NoticeDto.Update updateDto) {
        NoticeDto.Response responseDto = noticeService.updateNotice(noticeNo, updateDto);
        return ResponseEntity.ok(responseDto);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeNo}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeNo) {
        noticeService.deleteNotice(noticeNo);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}