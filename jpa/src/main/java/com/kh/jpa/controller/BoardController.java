package com.kh.jpa.controller;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin // Allow requests from other domains (e.g., frontend dev server)
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // Get paginated list of boards, optionally filtered by author name
    // Handles requests from BoardList.jsx and Mypage.jsx
    @GetMapping
    public ResponseEntity<PageResponse<BoardDto.Response>> getBoards(
            @PageableDefault(size = 5, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String author // 'author' parameter for Mypage.jsx filtering
    ) {
        return ResponseEntity.ok(boardService.getBoardList(pageable, author));
    }

    // Get a single board detail by its ID
    // Handles requests from BoardDetail.jsx
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable("id") Long boardNo) {
        return ResponseEntity.ok(boardService.getBoardDetail(boardNo));
    }

    // Create a new board
    // Handles requests from BoardCreate.jsx
    @PostMapping
    public ResponseEntity<BoardDto.Response> createBoard(
            @RequestBody BoardDto.Create boardCreateDto,
            @RequestHeader(value = "X-USER-ID") String currentUserId // Get user ID (email) from header
    ) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Or handle as appropriate
        }
        BoardDto.Response responseDto = boardService.createBoard(boardCreateDto, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // Delete a board by its ID
    // Handles requests from BoardDetail.jsx
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable("id") Long boardNo,
            @RequestHeader(value = "X-USER-ID") String currentUserId // Get user ID (email) from header
    ) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boardService.deleteBoard(boardNo, currentUserId);
        return ResponseEntity.noContent().build();
    }

    // Update an existing board
    // Handles requests from BoardEdit.jsx
    @PutMapping("/{id}")
    public ResponseEntity<BoardDto.Response> updateBoard(
            @PathVariable("id") Long boardNo,
            @RequestBody BoardDto.Update updateBoardDto,
            @RequestHeader(value = "X-USER-ID") String currentUserId // Get user ID (email) from header
    ) {
        if (currentUserId == null || currentUserId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        BoardDto.Response responseDto = boardService.updateBoard(boardNo, updateBoardDto, currentUserId);
        return ResponseEntity.ok(responseDto);
    }
}