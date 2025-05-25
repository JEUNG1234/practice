package com.kh.jpa.service;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.PollDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PollService {
    PollDto.Response createPoll(PollDto.Create createDto, String userId);

    PageResponse<PollDto.Response> getAllPolls(Pageable pageable);

    PageResponse<PollDto.Response> getHotPolls(Pageable pageable); // HOT 투표 조회

    PollDto.Response getPollById(Long pollId);

    PollDto.Response updatePoll(Long pollId, PollDto.Update updateDto, String userId);

    void deletePoll(Long pollId, String userId);

    PollDto.Response voteOnPoll(Long pollId, PollDto.VoteRequest voteRequest, String userId);
}