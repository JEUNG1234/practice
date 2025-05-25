package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import com.kh.jpa.repository.BoardRepository;
import com.kh.jpa.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BoardDto.Response> getBoardList(Pageable pageable, String authorName) {
        Page<Board> boardPage;
        if (authorName != null && !authorName.isEmpty()) {
            // Fetch boards by author's user name for Mypage.jsx
            boardPage = boardRepository.findByMemberUserName(authorName, pageable);
        } else {
            // Fetch all boards for BoardList.jsx
            boardPage = boardRepository.findAllByOrderByCreateDateDesc(pageable);
        }
        Page<BoardDto.Response> dtoPage = boardPage.map(BoardDto.Response::forList);
        return new PageResponse<>(dtoPage);
    }

    @Override
    @Transactional
    public BoardDto.Response getBoardDetail(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardNo));

        boardRepository.incrementViewCount(boardNo);
        // Re-fetch or manually update viewCount in the entity if needed immediately for the DTO
        Board updatedBoard = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardNo));

        return BoardDto.Response.fromEntity(updatedBoard);
    }

    @Override
    @Transactional
    public BoardDto.Response createBoard(BoardDto.Create createBoardDto, String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. ID: " + userId));

        Board board = createBoardDto.toEntity(member);
        Board savedBoard = boardRepository.save(board);
        return BoardDto.Response.fromEntity(savedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardNo, String userId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 게시글을 찾을 수 없습니다. ID: " + boardNo));

        if (board.getMember() == null || !board.getMember().getUserId().equals(userId)) {
            throw new SecurityException("게시글 삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public BoardDto.Response updateBoard(Long boardNo, BoardDto.Update boardUpdateDto, String userId) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("수정할 게시글을 찾을 수 없습니다. ID: " + boardNo));

        if (board.getMember() == null || !board.getMember().getUserId().equals(userId)) {
            throw new SecurityException("게시글 수정 권한이 없습니다.");
        }

        board.updateBoard(boardUpdateDto.getTitle(), boardUpdateDto.getBody());
        // The transaction will automatically commit changes to the board entity.
        return BoardDto.Response.fromEntity(board);
    }
}