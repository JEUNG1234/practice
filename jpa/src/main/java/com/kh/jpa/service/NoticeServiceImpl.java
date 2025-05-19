package com.kh.jpa.service;

import com.kh.jpa.dto.NoticeDto;
import com.kh.jpa.entity.Member;
import com.kh.jpa.entity.Notice;
import com.kh.jpa.repository.MemberRepository;
import com.kh.jpa.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    @Override
    public NoticeDto.Response createNotice(NoticeDto.Create createDto) {
        Member writer = memberRepository.findOne(createDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 회원을 찾을 수 없습니다: " + createDto.getUserId()));

        Notice notice = createDto.toEntity(writer);
        Notice savedNotice = noticeRepository.save(notice);
        return NoticeDto.Response.fromEntity(savedNotice);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeDto.Response getNoticeById(Long noticeNo) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 공지사항을 찾을 수 없습니다: " + noticeNo));
        return NoticeDto.Response.fromEntity(notice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NoticeDto.Response> getAllNotices() {
        return noticeRepository.findAll().stream()
                .map(NoticeDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto.Response updateNotice(Long noticeNo, NoticeDto.Update updateDto) {
        Notice notice = noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 번호의 공지사항을 찾을 수 없습니다: " + noticeNo));

        // 엔티티의 자체 업데이트 메소드 호출
        notice.updateDetails(updateDto.getNoticeTitle(), updateDto.getNoticeContent());

        // JPA의 변경 감지(dirty checking)에 의해 트랜잭션 종료 시점에 UPDATE 쿼리가 실행됩니다.
        // noticeRepository.save(notice)를 명시적으로 호출할 필요는 없지만, 호출해도 문제는 없습니다.
        // 명시적으로 호출하면 save 시점에 flush가 일어날 수 있습니다.
        return NoticeDto.Response.fromEntity(notice); // 변경된 notice를 반환
    }

    @Override
    public void deleteNotice(Long noticeNo) {
        if (!noticeRepository.existsById(noticeNo)) {
            throw new IllegalArgumentException("해당 번호의 공지사항을 찾을 수 없습니다: " + noticeNo);
        }
        noticeRepository.deleteById(noticeNo);
    }
}