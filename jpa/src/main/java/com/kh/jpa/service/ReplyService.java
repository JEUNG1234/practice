package com.kh.jpa.service;

import com.kh.jpa.dto.ReplyDto;
import java.util.List;

public interface ReplyService {
    // 댓글 생성
    ReplyDto.Response createReply(Long boardId, ReplyDto.Create createDto, String userId);

    // 특정 게시글의 모든 댓글 조회
    List<ReplyDto.Response> getRepliesByBoardId(Long boardId);

    // 댓글 수정
    ReplyDto.Response updateReply(Long replyId, ReplyDto.Update updateDto, String userId);

    // 댓글 삭제
    void deleteReply(Long replyId, String userId);
}