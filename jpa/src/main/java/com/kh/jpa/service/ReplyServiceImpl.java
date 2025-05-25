package com.kh.jpa.service;

import com.kh.jpa.dto.ReplyDto;
import com.kh.jpa.entity.Board;
import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Reply;
import com.kh.jpa.repository.BoardRepository;
import com.kh.jpa.repository.MemberRepository;
import com.kh.jpa.repository.ReplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    @Override
    public ReplyDto.Response createReply(Long boardId, ReplyDto.Create createDto, String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. ID: " + userId));
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + boardId));

        Reply reply = createDto.toEntity(board, member);
        Reply savedReply = replyRepository.save(reply);
        return ReplyDto.Response.fromEntity(savedReply);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReplyDto.Response> getRepliesByBoardId(Long boardId) {
        // Board 객체를 먼저 조회하거나, boardId로 직접 조회하는 repository 메소드 사용
        // 여기서는 boardId로 직접 조회
        List<Reply> replies = replyRepository.findByBoardBoardNoOrderByCreateDateAsc(boardId);
        return replies.stream()
                .map(ReplyDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ReplyDto.Response updateReply(Long replyId, ReplyDto.Update updateDto, String userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + replyId));

        if (reply.getMember() == null || !reply.getMember().getUserId().equals(userId)) {
            throw new SecurityException("댓글 수정 권한이 없습니다.");
        }

        reply.updateContent(updateDto.getContent());
        // @Transactional에 의해 변경 감지되어 자동 업데이트됨
        return ReplyDto.Response.fromEntity(reply);
    }

    @Override
    public void deleteReply(Long replyId, String userId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + replyId));

        if (reply.getMember() == null || !reply.getMember().getUserId().equals(userId)) {
            throw new SecurityException("댓글 삭제 권한이 없습니다.");
        }
        replyRepository.delete(reply);
    }
}