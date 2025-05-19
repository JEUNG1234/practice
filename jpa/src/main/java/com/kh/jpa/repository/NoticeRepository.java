package com.kh.jpa.repository;

import com.kh.jpa.entity.Notice;
import java.util.List;
import java.util.Optional;

public interface NoticeRepository {
    Notice save(Notice notice);
    Optional<Notice> findById(Long noticeNo);
    List<Notice> findAll();
    void deleteById(Long noticeNo);
    boolean existsById(Long noticeNo); // 서비스 레이어에서 사용했으므로 추가
    // 필요에 따라 다른 메소드 시그니처 추가
}


//package com.kh.jpa.repository;
//
//import com.kh.jpa.entity.Notice;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface NoticeRepository extends JpaRepository<Notice, Long> {
//    // JpaRepository가 기본적인 CRUD 메소드를 제공합니다 (e.g., save, findById, findAll, deleteById)
//    // 필요에 따라 커스텀 쿼리 메소드를 추가할 수 있습니다.
//}