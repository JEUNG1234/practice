package com.kh.jpa.service;

import com.kh.jpa.dto.NoticeDto;
import java.util.List;

public interface NoticeService {
    NoticeDto.Response createNotice(NoticeDto.Create createDto);
    NoticeDto.Response getNoticeById(Long noticeNo);
    List<NoticeDto.Response> getAllNotices();
    NoticeDto.Response updateNotice(Long noticeNo, NoticeDto.Update updateDto);
    void deleteNotice(Long noticeNo);
}