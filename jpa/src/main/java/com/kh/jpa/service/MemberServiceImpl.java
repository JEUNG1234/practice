package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.entity.Member;
import com.kh.jpa.repository.MemberRepository; // Spring Data JPA 인터페이스 사용
import lombok.RequiredArgsConstructor;
// import org.springframework.security.crypto.password.PasswordEncoder; // 암호화 사용 안 함
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// import java.util.Optional; // Optional은 MemberRepository 반환 타입에서 사용됨
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository; // JpaRepository를 상속받은 MemberRepository 주입
    // private final PasswordEncoder passwordEncoder; // 암호화 사용 안 함

    @Override
    public MemberDto.Response createMember(MemberDto.Create createDto) {
        // 이메일(userId) 중복 검사 (Repository의 existsByEmail 사용)
        if (memberRepository.existsByEmail(createDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일(ID)입니다: " + createDto.getEmail());
        }

        Member member = createDto.toEntity(); // DTO에서 평문 비밀번호를 사용
        Member savedMember = memberRepository.save(member); // JpaRepository의 save 사용
        return MemberDto.Response.fromEntity(savedMember);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto.LoginResponse login(MemberDto.LoginRequest loginRequest) {
        Member member = memberRepository.findByUserId(loginRequest.getEmail()) // Repository의 findByUserId 사용
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일(ID)입니다."));

        // 평문 비밀번호 비교
        if (!loginRequest.getPassword().equals(member.getUserPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성 로직 (현재는 더미 토큰)
        // TODO: 실제 JWT 라이브러리를 사용하여 토큰 생성 로직 구현 필요
        String token = "dummy-jwt-token-for-" + member.getUserId();

        return MemberDto.LoginResponse.builder()
                .token(token)
                .userInfo(MemberDto.Response.fromEntity(member))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto.Response findMember(String userId) {
        return memberRepository.findByUserId(userId) // Repository의 findByUserId 사용
                .map(MemberDto.Response::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + userId));
    }

    @Override
    public MemberDto.Response updateMember(String userId, MemberDto.Update updateDto) {
        Member member = memberRepository.findByUserId(userId) // Repository의 findByUserId 사용
                .orElseThrow(() -> new IllegalArgumentException("수정할 회원을 찾을 수 없습니다. ID: " + userId));

        // 비밀번호 변경 로직 (평문 비교 및 저장)
        if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty()) {
            if (updateDto.getCurrentPassword() == null || updateDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
            }
            if (!updateDto.getCurrentPassword().equals(member.getUserPwd())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            member.changePassword(updateDto.getNewPassword()); // 평문으로 새 비밀번호 저장
        }

        // Mypage.jsx 에서는 이름 변경 UI가 없으므로, 사용자 정보 업데이트 로직은 현재 없음
        // member.updateUserInfo(...);

        // JpaRepository를 사용하면 @Transactional에 의해 변경 감지가 동작하여 자동 업데이트됨
        return MemberDto.Response.fromEntity(member);
    }

    @Override
    public void deleteMember(String userId) {
        Member member = memberRepository.findByUserId(userId) // Repository의 findByUserId 사용
                .orElseThrow(() -> new IllegalArgumentException("삭제할 회원을 찾을 수 없습니다. ID: " + userId));
        memberRepository.delete(member); // JpaRepository의 delete 사용
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto.Response> findAllMember() {
        return memberRepository.findAll().stream() // JpaRepository의 findAll 사용
                .map(MemberDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto.Response> findByName(String name) {
        return memberRepository.findByUserNameContaining(name).stream() // Repository의 findByUserNameContaining 사용
                .map(MemberDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    // MemberService 인터페이스에 추가된 메서드 구현
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return memberRepository.existsByEmail(email); // Repository의 existsByEmail 사용
    }
}