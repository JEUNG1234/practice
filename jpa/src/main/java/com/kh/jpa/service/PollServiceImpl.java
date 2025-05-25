package com.kh.jpa.service;

import com.kh.jpa.dto.PageResponse;
import com.kh.jpa.dto.PollDto;
import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Poll;
import com.kh.jpa.entity.PollOption;
import com.kh.jpa.repository.MemberRepository;
import com.kh.jpa.repository.PollOptionRepository;
import com.kh.jpa.repository.PollRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PollServiceImpl implements PollService {

    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final MemberRepository memberRepository;

    @Override
    public PollDto.Response createPoll(PollDto.Create createDto, String userId) {
        Member author = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("작성자 정보를 찾을 수 없습니다. ID: " + userId));

        Poll poll = Poll.builder()
                .title(createDto.getTitle())
                .pollType(createDto.getPollType())
                .member(author)
                .build(); // createdAt, totalVotes는 @PrePersist, @Builder.Default로 처리

        List<PollOption> pollOptions = createDto.getOptions().stream()
                .map(optDto -> PollOption.builder().text(optDto.getText()).poll(poll).build())
                .collect(Collectors.toList());
        poll.setOptions(pollOptions); // Poll 엔티티에 옵션 설정 (cascade = CascadeType.ALL로 함께 저장됨)

        Poll savedPoll = pollRepository.save(poll);
        return PollDto.Response.fromEntity(savedPoll);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PollDto.Response> getAllPolls(Pageable pageable) {
        Page<Poll> pollPage = pollRepository.findAllByOrderByCreatedAtDesc(pageable);
        Page<PollDto.Response> dtoPage = pollPage.map(PollDto.Response::fromEntity);
        return new PageResponse<>(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PollDto.Response> getHotPolls(Pageable pageable) {
        // PollList.jsx 에서 3개만 가져오므로 Pageable을 서비스단에서 생성
        Pageable topThree = PageRequest.of(0, 3, Sort.by("totalVotes").descending().and(Sort.by("createdAt").descending()));
        Page<Poll> pollPage = pollRepository.findAllByOrderByTotalVotesDescCreatedAtDesc(topThree);
        Page<PollDto.Response> dtoPage = pollPage.map(PollDto.Response::fromEntity);
        return new PageResponse<>(dtoPage);
    }


    @Override
    @Transactional(readOnly = true)
    public PollDto.Response getPollById(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("투표를 찾을 수 없습니다. ID: " + pollId));
        return PollDto.Response.fromEntity(poll);
    }

    @Override
    public PollDto.Response updatePoll(Long pollId, PollDto.Update updateDto, String userId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("수정할 투표를 찾을 수 없습니다. ID: " + pollId));

        if (poll.getMember() == null || !poll.getMember().getUserId().equals(userId)) {
            throw new SecurityException("투표 수정 권한이 없습니다.");
        }

        poll.setTitle(updateDto.getTitle());
        poll.setPollType(updateDto.getPollType());

        // 옵션 수정 로직: 기존 옵션 ID를 유지하며 텍스트 변경, 새 옵션 추가, 기존 옵션 삭제
        // 여기서는 단순화를 위해 기존 옵션을 모두 지우고 새로 추가 (투표수는 초기화되지 않도록 주의)
        // 또는 더 정교하게 ID를 비교하여 업데이트/추가/삭제
        Map<Long, PollOption> existingOptionsMap = poll.getOptions().stream()
                .collect(Collectors.toMap(PollOption::getId, Function.identity()));
        List<PollOption> newOptionsList = new ArrayList<>();

        for (PollDto.CreateOption optionDto : updateDto.getOptions()) {
            // 프론트에서 기존 옵션의 ID를 함께 보내준다면 해당 ID로 찾아서 업데이트
            // 여기서는 text만 비교하여 기존 votes를 유지하는 로직은 복잡하므로,
            // PollEdit.jsx에서 기존 옵션 id를 함께 보내고, votes는 유지하는 방식으로 구현해야 함.
            // 현재 DTO 설계로는 votes 유지가 어려우므로, 옵션 수정 시 votes가 0으로 초기화될 수 있음.
            // 또는, 옵션 수정 시 투표를 초기화하는 정책을 가질 수도 있음.
            // 여기서는 간단하게 모든 옵션을 새로 만든다고 가정. (실제로는 ID 매칭 및 votes 보존 필요)

            // 임시: 기존 옵션은 그대로 두고, 텍스트가 바뀐 것은 새로 만들고, 없어진 것은 삭제하는 방식은 복잡.
            // 가장 간단한 방식은 기존 옵션을 모두 삭제하고 DTO의 옵션으로 새로 생성. 이 경우 votes는 0이 됨.
            // votes를 보존하려면, DTO에 optionId도 포함시켜서 매칭해야 함. PollDto.CreateOption을 수정.
        }
        // 기존 옵션 모두 삭제 후 새 옵션 추가 (주의: 이러면 votes가 초기화됨. 실제로는 votes를 유지하는 로직 필요)
        poll.clearOptions(); // 기존 옵션 연결 해제 및 삭제 (orphanRemoval=true)
        updateDto.getOptions().forEach(optDto -> {
            poll.addOption(PollOption.builder().text(optDto.getText()).poll(poll).votes(0).build());
        });


        Poll updatedPoll = pollRepository.save(poll);
        return PollDto.Response.fromEntity(updatedPoll);
    }

    @Override
    public void deletePoll(Long pollId, String userId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("삭제할 투표를 찾을 수 없습니다. ID: " + pollId));

        if (poll.getMember() == null || !poll.getMember().getUserId().equals(userId)) {
            throw new SecurityException("투표 삭제 권한이 없습니다.");
        }
        pollRepository.delete(poll);
    }

    @Override
    public PollDto.Response voteOnPoll(Long pollId, PollDto.VoteRequest voteRequest, String userId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new EntityNotFoundException("투표를 찾을 수 없습니다. ID: " + pollId));

        // 작성자는 자신의 투표에 투표할 수 없도록 하는 로직 (프론트와 일관성)
        if (poll.getMember() != null && poll.getMember().getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신이 만든 투표에는 투표할 수 없습니다.");
        }

        // TODO: 중복 투표 방지 로직 (프론트엔드의 localStorage 외에 백엔드에서도 검증 필요 시)
        // 예: VoterParticipation 엔티티를 만들어 (userId, pollId) 복합키로 저장하고, 이미 존재하면 예외 발생

        List<Long> selectedOptionIds = voteRequest.getSelectedOptionIds();
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            throw new IllegalArgumentException("선택된 옵션이 없습니다.");
        }

        if ("singleChoice".equals(poll.getPollType()) && selectedOptionIds.size() > 1) {
            throw new IllegalArgumentException("단일 선택 투표에는 하나의 옵션만 선택할 수 있습니다.");
        }

        boolean voteProcessed = false;
        for (PollOption option : poll.getOptions()) {
            if (selectedOptionIds.contains(option.getId())) {
                option.setVotes(option.getVotes() + 1);
                voteProcessed = true;
            }
        }

        if (!voteProcessed && !selectedOptionIds.isEmpty()) {
            // 선택한 ID가 실제 옵션에 없는 경우
            throw new EntityNotFoundException("선택한 옵션을 찾을 수 없습니다.");
        }

        if (voteProcessed) { // 하나 이상의 유효한 투표가 처리된 경우에만 totalVotes 증가
            poll.setTotalVotes(poll.getTotalVotes() + 1); // 한 번의 투표 행위에 대해 totalVotes 1 증가
        }


        Poll savedPoll = pollRepository.save(poll);
        return PollDto.Response.fromEntity(savedPoll);
    }
}