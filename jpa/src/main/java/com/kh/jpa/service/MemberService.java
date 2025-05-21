package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import java.util.List;

public interface MemberService {
    String createMember(MemberDto.Create createDto);
    MemberDto.Response findMember(String userId);
    MemberDto.Response updateMember(String userId, MemberDto.Update updateDto);
    void deleteMember(String userId);
    List<MemberDto.Response> findAllMember();
    List<MemberDto.Response> findByName(String name);
}