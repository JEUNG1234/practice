package com.kh.board.mapper;

import com.kh.board.entity.Board;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    List<Board> findAll();
}
