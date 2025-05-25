package com.kh.jpa.repository;

import com.kh.jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> { // JpaRepository<엔티티, PK타입> 상속

    // 사용자 ID(이메일)로 회원 조회 (로그인, 중복 체크 등에 사용)
    // MemberServiceImpl에서 findOne 대신 이 메서드를 사용합니다.
    Optional<Member> findByUserId(String userId);

    // 이메일로 회원 조회 (회원가입 시 중복 체크에 사용될 수 있음)
    Optional<Member> findByEmail(String email);

    // 사용자 이름으로 회원 목록 조회 (Like 검색)
    List<Member> findByUserNameContaining(String userName);

    // 이메일 존재 여부 확인 (회원가입 시 중복 체크용)
    boolean existsByEmail(String email); // MemberServiceImpl에서 사용
}