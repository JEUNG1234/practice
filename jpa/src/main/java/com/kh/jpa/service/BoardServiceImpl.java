package com.kh.jpa.service;

import com.kh.jpa.dto.BoardDto;
import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
// import com.kh.jpa.entity.BoardTag; // 태그 제외
// import com.kh.jpa.entity.Tag;      // 태그 제외
import com.kh.jpa.enums.CommonEnums;
import com.kh.jpa.repository.BoardRepository;    // Spring Data JPA Repository 사용
import com.kh.jpa.repository.MemberRepository;   // Spring Data JPA Repository 사용
// import com.kh.jpa.repository.TagRepository;   // 태그 제외
import jakarta.persistence.EntityNotFoundException;
// import java.io.File; // 파일 제외
// import java.io.IOException; // 파일 제외
// import java.util.UUID; // 파일 제외
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// PageResponse DTO 임시 정의 (실제로는 별도 파일 com.kh.jpa.dto.PageResponse)
// BoardController.java에서 PageResponse<T>를 사용하므로 임시로 정의
// package com.kh.jpa.dto; << 이 패키지에 실제 파일이 있어야 함
import lombok.Getter; // PageResponse용
// import java.util.List; // PageResponse용 -> BoardService 인터페이스로 이동

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨에 기본 트랜잭션 적용
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository; // Spring Data JPA 인터페이스로 변경
    private final MemberRepository memberRepository; // Spring Data JPA 인터페이스로 변경
    // private final TagRepository tagRepository; // 태그 제외
    // private final String UPLOAD_PATH = "C:\\dev_tool\\"; // 파일 제외

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BoardDto.Response> getBoardList(Pageable pageable) {
        // BoardRepositoryImpl의 findByStatus 대신 JpaRepository의 findAll 사용 또는 커스텀 쿼리
        // 프론트엔드는 상태(Y/N) 구분 없이 모든 게시글을 최신순으로 보여주므로
        // Status.Y 필터링이 필요하다면 BoardRepository에 메소드 추가 (예: findAllByStatusOrderByCreateDateDesc)
        // 지금은 findAllByOrderByCreateDateDesc 사용 (이전 답변에서 BoardRepository에 정의)
        Page<Board> boardPage = boardRepository.findAllByOrderByCreateDateDesc(pageable);
        // Page<BoardDto.Response> dtoPage = boardPage.map(BoardDto.Response::toSimpleDto); // 기존 코드
        Page<BoardDto.Response> dtoPage = boardPage.map(BoardDto.Response::forList); // 본문 제외 목록용 DTO 사용
        return new PageResponse<>(dtoPage);
    }

    @Override
    @Transactional // 조회수 증가 로직이 포함될 수 있으므로 readOnly=false 유지 또는 분리
    public BoardDto.Response getBoardDetail(Long boardNo) {
        Board board = boardRepository.findById(boardNo) // JpaRepository의 findById 사용
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardNo));

        boardRepository.incrementViewCount(boardNo); // 조회수 증가 (BoardRepository에 정의된 @Modifying 쿼리 사용)
        // incrementViewCount 후 board 객체를 다시 로드하거나, 반환된 int 값을 사용해 viewCount 업데이트 필요 없음
        // (쿼리가 직접 DB를 업데이트하므로)
        // 다만, DTO로 변환 시 최신 상태를 반영하기 위해 다시 조회하거나 board 객체를 직접 업데이트

        // 가장 최신 상태의 board를 가져오기 위해 다시 조회 (또는 board.setViewCount 직접 업데이트)
        Board updatedBoard = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardNo));

        return BoardDto.Response.fromEntity(updatedBoard); // 기존 코드
    }

    @Override
    @Transactional
    public BoardDto.Response createBoard(BoardDto.Create createBoardDto, String userId) { // IOException 제거, userId 파라미터 추가
        Member member = memberRepository.findByUserId(userId) // JpaRepository 사용
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. ID: " + userId));

        Board board = createBoardDto.toEntity(member); // DTO에서 Member를 받도록 수정했음
        // board.changeMember(member); // toEntity에서 처리하도록 변경
        // 파일 처리 로직 제거
        // 태그 처리 로직 제거

        Board savedBoard = boardRepository.save(board); // JpaRepository의 save 사용, ID 반환 안 함
        return BoardDto.Response.fromEntity(savedBoard); // 생성된 게시글 정보 반환
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardNo, String userId) { // userId 파라미터 추가
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 게시글을 찾을 수 없습니다. ID: " + boardNo));

        if (!board.getMember().getUserId().equals(userId)) {
            throw new SecurityException("게시글 삭제 권한이 없습니다."); // 또는 다른 적절한 예외
        }

        // 파일 삭제 로직 제거
        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public BoardDto.Response updateBoard(Long boardNo, BoardDto.Update boardUpdateDto, String userId) { // IOException 제거, userId 파라미터 추가
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("수정할 게시글을 찾을 수 없습니다. ID: " + boardNo));

        if (!board.getMember().getUserId().equals(userId)) {
            throw new SecurityException("게시글 수정 권한이 없습니다."); // 또는 다른 적절한 예외
        }

        board.updateBoard(boardUpdateDto.getTitle(), boardUpdateDto.getBody()); // Entity 내 업데이트 메소드 사용
        // board.changeContent(boardUpdate.getBoard_content()); // 기존 코드
        // board.changeTitle(boardUpdate.getBoard_title());   // 기존 코드

        // 파일 처리 로직 제거
        // 태그 처리 로직 제거 (기존 clear 및 재추가 로직)

        // @Transactional에 의해 변경 감지로 업데이트됨. 명시적 save는 선택.
        Board updatedBoard = boardRepository.save(board);
        return BoardDto.Response.fromEntity(updatedBoard);
    }
}