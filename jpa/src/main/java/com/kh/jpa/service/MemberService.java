package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import java.util.List;

public interface MemberService {
    MemberDto.Response createMember(MemberDto.Create createDto);
    MemberDto.LoginResponse login(MemberDto.LoginRequest loginRequest);
    MemberDto.Response findMember(String userId);
    MemberDto.Response updateMember(String userId, MemberDto.Update updateDto);
    void deleteMember(String userId);
    List<MemberDto.Response> findAllMember();
    List<MemberDto.Response> findByName(String name);

    // 이메일 중복 체크용 메서드 추가
    boolean checkEmailExists(String email);
}