package com.kh.board.service;

import com.kh.board.entity.Board;
import java.util.List;

public interface BoardService {
    Board findOne(Long boardId);
    List<Board> findAll();
    int save(Board board);
    int delete(Long boardId);
    Long update(Board board);
}
