package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import java.util.List;

public interface MemberService {
    // 회원가입 (프론트엔드 Register.jsx 에 맞게 응답 타입을 MemberDto.Response 로 변경 고려)
    // 기존: String createMember(MemberDto.Create createDto);
    MemberDto.Response createMember(MemberDto.Create createDto); // 응답으로 사용자 정보 반환

    // 로그인 (프론트엔드 Login.jsx 에 맞게 신규 추가)
    MemberDto.LoginResponse login(MemberDto.LoginRequest loginRequest);

    // 회원 정보 조회 (기존 findMember 유지)
    MemberDto.Response findMember(String userId);

    // 회원 정보 수정 (기존 updateMember 유지)
    MemberDto.Response updateMember(String userId, MemberDto.Update updateDto);

    // 회원 삭제 (기존 deleteMember 유지)
    void deleteMember(String userId);

    // 전체 회원 조회 (기존 findAllMember 유지)
    List<MemberDto.Response> findAllMember();

    // 이름으로 회원 검색 (기존 findByName 유지)
    List<MemberDto.Response> findByName(String name);
}