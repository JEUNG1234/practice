package com.kh.jpa.service;

import com.kh.jpa.dto.MemberDto;
import com.kh.jpa.entity.Member;
import com.kh.jpa.repository.MemberRepository; // 기존 MemberRepository 인터페이스 사용
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // 클래스 레벨 트랜잭션, 기본적으로 대부분의 메소드에 적용
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository; // MemberRepositoryImpl이 주입됨
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDto.Response createMember(MemberDto.Create createDto) {
        // MemberRepositoryImpl에는 existsByUserId 와 같은 메소드가 없으므로, findOne으로 확인
        if (memberRepository.findOne(createDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일(ID)입니다: " + createDto.getEmail());
        }
        // Member 엔티티의 email 필드도 unique 제약조건이 있다면 추가 체크 필요 (현재는 userId와 동일하게 사용)

        String encryptedPassword = passwordEncoder.encode(createDto.getPassword());
        Member member = createDto.toEntity(encryptedPassword);
        memberRepository.save(member); // MemberRepositoryImpl의 save 호출
        // 저장 후 Member 객체는 ID 등 영속화된 상태를 가짐 (만약 save가 void라면 findOne으로 다시 조회)
        return MemberDto.Response.fromEntity(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto.LoginResponse login(MemberDto.LoginRequest loginRequest) {
        Member member = memberRepository.findOne(loginRequest.getEmail()) // MemberRepositoryImpl의 findOne 호출
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일(ID)입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getUserPwd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = "dummy-jwt-token-for-" + member.getUserId(); // 실제 JWT 토큰 생성 로직

        return MemberDto.LoginResponse.builder()
                .token(token)
                .userInfo(MemberDto.Response.fromEntity(member))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDto.Response findMember(String userId) {
        return memberRepository.findOne(userId) // MemberRepositoryImpl의 findOne 호출
                .map(MemberDto.Response::fromEntity) // DTO 변환 방식 통일 (기존 fromEntity 사용)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + userId));
    }

    @Override
    public MemberDto.Response updateMember(String userId, MemberDto.Update updateDto) {
        Member member = memberRepository.findOne(userId) // MemberRepositoryImpl의 findOne 호출
                .orElseThrow(() -> new IllegalArgumentException("수정할 회원을 찾을 수 없습니다. ID: " + userId));

        // 비밀번호 변경 로직
        if (updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty()) {
            if (updateDto.getCurrentPassword() == null || updateDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("현재 비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(updateDto.getCurrentPassword(), member.getUserPwd())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            member.changePassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }

        // 추가 프로필 정보 업데이트 (Mypage.jsx에 맞춰서)
        // Member 엔티티의 updateUserInfo 메소드는 여러 필드를 받지만, Mypage.jsx는 이름, 이메일 readOnly
        // 필요하다면 MemberDto.Update에 있는 필드만 사용하도록 Member 엔티티의 updateUserInfo 메소드 수정 또는 새 메소드 추가
        member.updateUserInfo(
                updateDto.getUserName(), // MemberDto.Update에 userName 필드가 있다면
                // updateDto.getPhone(), // MemberDto.Update에 phone 필드가 있다면
                // updateDto.getAddress(), // MemberDto.Update에 address 필드가 있다면
                // updateDto.getAge(), // MemberDto.Update에 age 필드가 있다면
                // updateDto.getGender() // MemberDto.Update에 gender 필드가 있다면
        );
        // memberRepository.save(member); // MemberRepositoryImpl의 save는 void 이므로, 변경 감지로 처리되거나 merge 필요
        // @Transactional이므로 변경 감지 동작
        return MemberDto.Response.fromEntity(member);
    }

    @Override
    public void deleteMember(String userId) {
        Member member = memberRepository.findOne(userId) // MemberRepositoryImpl의 findOne 호출
                .orElseThrow(() -> new IllegalArgumentException("삭제할 회원을 찾을 수 없습니다. ID: " + userId));
        memberRepository.delete(member); // MemberRepositoryImpl의 delete 호출
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto.Response> findAllMember() {
        return memberRepository.findAll().stream() // MemberRepositoryImpl의 findAll 호출
                .map(MemberDto.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberDto.Response> findByName(String name) {
        return memberRepository.findByName(name).stream() // MemberRepositoryImpl의 findByName 호출
                .map(MemberDto.Response::fromEntity)
                .collect(Collectors.toList());
    }
}