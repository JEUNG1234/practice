package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
// import com.kh.jpa.entity.Board; // Page<BoardDto.Response> 사용으로 변경
import com.kh.jpa.service.PageResponse; // 이전 답변의 임시 PageResponse DTO 또는 실제 경로
import org.springframework.data.domain.Pageable;
// import java.io.IOException; // 파일 처리 제외

public interface BoardService {
    PageResponse<BoardDto.Response> getBoardList(Pageable pageable); // 응답 DTO 사용

    BoardDto.Response getBoardDetail(Long boardNo);

    // Long createBoard(BoardDto.Create boardDto) throws IOException;
    BoardDto.Response createBoard(BoardDto.Create boardCreateDto, String userId); // 작성자 ID 추가, IOException 제거, 응답 DTO

    void deleteBoard(Long boardNo, String userId); // 작성자 ID 추가 (권한 확인용)

    // BoardDto.Response updateBoard(Long boardNo, BoardDto.Update boardDto) throws IOException;
    BoardDto.Response updateBoard(Long boardNo, BoardDto.Update boardUpdateDto, String userId); // 작성자 ID 추가, IOException 제거
}