package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BoardService {
    PageResponse<BoardDto.Response> getBoardList(Pageable pageable, String authorName);

    BoardDto.Response getBoardDetail(Long boardNo);

    BoardDto.Response createBoard(BoardDto.Create boardCreateDto, String userId);

    void deleteBoard(Long boardNo, String userId);

    BoardDto.Response updateBoard(Long boardNo, BoardDto.Update boardUpdateDto, String userId);
}