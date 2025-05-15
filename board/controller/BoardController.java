package com.kh.board.controller;

import com.kh.board.controller.dto.request.BoardRequest;
import com.kh.board.controller.dto.response.BoardResponse;
import com.kh.board.entity.Board;
import com.kh.board.mapper.BoardMapper;
import com.kh.board.service.BoardService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //게시글목록을 가져오는 api
    @GetMapping
    public ResponseEntity<List<BoardResponse.SimpleDTO>> getBoardList() {
        List<Board> boardList = boardService.findAll();

        List<BoardResponse.SimpleDTO> result = new ArrayList<>();
        for (Board board : boardList) {
            result.add(BoardResponse.SimpleDTO.formEntity(board));
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponse.DetailDTO> getBoard(@PathVariable Long boardId) {
        Board board = boardService.findOne(boardId);
        BoardResponse.DetailDTO result = BoardResponse.DetailDTO.formEntity(board);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createBoard(BoardRequest.CreateDTO request, MultipartFile upfile) throws IOException {

        if (request == null || request.getUser_id() == null) {
            throw new RuntimeException("check value");
        }

        if (!upfile.isEmpty()) {
            File file = new File("C:\\workspace\\11_SpringBoot\\board\\src\\main\\resources\\uploads", upfile.getOriginalFilename());
            upfile.transferTo(file);

            request.setFile_name("/uploads/" + upfile.getOriginalFilename());
        }

        Board board = request.toEntity();
        int result = boardService.save(board);

        if(result > 0){
            return new ResponseEntity<>("게시글 등록 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("게시글 등록 실패", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long boardId) {
        int result = boardService.delete(boardId);
        return new ResponseEntity<>(result + "개 게시글 삭제완료", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Long> updateBoard(BoardRequest.UpdateDTO request, MultipartFile upfile) throws IOException {

        if (upfile != null && !upfile.isEmpty()) {
            File file = new File("C:\\workspace\\11_SpringBoot\\board\\src\\main\\resources\\uploads", upfile.getOriginalFilename());
            upfile.transferTo(file);

            request.setOrigin_file("/uploads/" + upfile.getOriginalFilename());
        }

        Board board = request.toEntity();
        System.out.println(board);

        Long boardId = boardService.update(board);
        return new ResponseEntity<>(boardId, HttpStatus.OK);
    }
}
