package com.kh.jpa.controller;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // @CrossOrigin 추가

@CrossOrigin // 모든 출처에서의 요청 허용 (개발 편의상. 실제 운영 시에는 출처 명시 권장)
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 API (프론트엔드 Register.jsx 와 연동)
    // POST /api/members
    @PostMapping
    public ResponseEntity<MemberDto.Response> registerMember(@RequestBody MemberDto.Create createDto) {
        MemberDto.Response responseDto = memberService.createMember(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인 API (프론트엔드 Login.jsx 와 연동)
    // POST /api/members/login
    @PostMapping("/login")
    public ResponseEntity<MemberDto.LoginResponse> login(@RequestBody MemberDto.LoginRequest loginRequest) {
        MemberDto.LoginResponse loginResponse = memberService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    // 회원 단건 조회 API 또는 이메일 중복/존재 확인 API
    // GET /api/members/{userIdOrEmail} -> 경로를 명확히 분리하거나 파라미터로 구분하는 것이 좋음
    // 여기서는 프론트엔드의 이메일 중복체크 요청을 /api/members?email=... 로 처리
    // 회원 ID로 조회는 /api/members/{userId} 사용

    // 회원 ID로 조회
    @GetMapping("/{userId}")
    public ResponseEntity<MemberDto.Response> getMemberByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(memberService.findMember(userId));
    }

    // 전체 회원 조회 또는 이메일로 존재 여부 확인
    // GET /api/members
    // GET /api/members?email=test@example.com (이메일 중복/존재 확인)
    @GetMapping
    public ResponseEntity<?> getAllMembersOrCheckEmail(@RequestParam(required = false) String email) {
        if (email != null && !email.isEmpty()) {
            // 이메일 중복(존재) 확인 로직
            boolean exists = memberService.checkEmailExists(email);
            // 프론트엔드 Register.jsx 에서는 res.data.length > 0 으로 이미 사용 중인지 판단
            // 따라서, 존재하면 회원 정보를 (비록 1개라도) List로 감싸서 보내거나,
            // 아니면 명시적으로 다른 DTO나 상태로 응답하는 것이 좋음.
            // 여기서는 프론트 로직을 최소한으로 변경하기 위해, 존재하면 임의의 MemberDto.Response를 포함한 리스트로 응답 (실제로는 해당 사용자 정보)
            // 존재하지 않으면 빈 리스트 응답.
            if (exists) {
                // 이미 존재하는 이메일이므로, 프론트에서 `existingUsersRes.data.length > 0` 조건을 만족시키기 위해
                // 해당 사용자 정보를 List에 담아 반환하거나, 최소한 비어있지 않은 List를 반환.
                // 여기서는 간단히 해당 사용자 정보를 찾아 List로 반환. (실제로는 이렇게 사용자 정보를 노출하는 것은 좋지 않음)
                // 더 나은 방법은 boolean 값이나 특정 DTO로 응답하는 것.
                MemberDto.Response member = memberService.findMember(email); // 이메일이 userId와 같다고 가정
                return ResponseEntity.ok(List.of(member));
            } else {
                return ResponseEntity.ok(List.of()); // 존재하지 않으면 빈 리스트
            }
        }
        // email 파라미터가 없으면 전체 회원 목록 반환
        return ResponseEntity.ok(memberService.findAllMember());
    }


    // 회원 정보 수정 API (프론트엔드 Mypage.jsx 와 연동)
    // PUT /api/members/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<MemberDto.Response> updateMember(
            @PathVariable String userId,
            @RequestBody MemberDto.Update updateDto) {
        // TODO: 인증된 사용자와 userId 일치 여부 확인 로직 추가
        return ResponseEntity.ok(memberService.updateMember(userId, updateDto));
    }

    // 회원 삭제 API
    // DELETE /api/members/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String userId) {
        // TODO: 인증된 사용자와 userId 일치 여부 확인 로직 추가
        memberService.deleteMember(userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 이름으로 회원 검색 API
    // GET /api/members/search/name?name={name}
    @GetMapping("/search/name")
    public ResponseEntity<List<MemberDto.Response>> searchMemberByName(@RequestParam String name) {
        return ResponseEntity.ok(memberService.findByName(name));
    }
}