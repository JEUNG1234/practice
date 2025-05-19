package com.kh.jpa.repository;

import com.kh.jpa.entity.Notice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository; // @Repository 어노테이션 추가

import java.util.List;
import java.util.Optional;

@Repository // Spring Bean으로 등록
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (또는 @PersistenceContext 사용 시 필요 없을 수 있음)
public class NoticeRepositoryImpl implements NoticeRepository {

    @PersistenceContext // EntityManager 주입
    private EntityManager em;

    @Override
    public Notice save(Notice notice) {
        if (notice.getNoticeNo() == null) { // 새 엔티티인 경우
            em.persist(notice);
            return notice;
        } else { // 이미 존재하는 엔티티의 업데이트인 경우
            return em.merge(notice);
        }
    }

    @Override
    public Optional<Notice> findById(Long noticeNo) {
        Notice notice = em.find(Notice.class, noticeNo);
        return Optional.ofNullable(notice);
    }

    @Override
    public List<Notice> findAll() {
        //JPQL : 엔티티기반 쿼리를 전달하는 방법
        return em.createQuery("SELECT n FROM Notice n", Notice.class)
                .getResultList();
    }

    @Override
    public void deleteById(Long noticeNo) {
        findById(noticeNo).ifPresent(em::remove);
    }

    @Override
    public boolean existsById(Long noticeNo) {
        return findById(noticeNo).isPresent();
    }
}