package com.youth.food_sharing.member.service;

import com.youth.food_sharing.common.security.TokenProvider;
import com.youth.food_sharing.member.domain.Member;
import com.youth.food_sharing.member.dto.LoginRequest;
import com.youth.food_sharing.member.dto.SignUpRequest;
import com.youth.food_sharing.member.dto.TokenResponse;
import com.youth.food_sharing.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 회원 비즈니스 로직 서비스 (Java)
 *
 * [의존성 주입 방식]
 * Lombok 미사용 원칙에 따라 직접 생성자를 작성한다.
 * 단일 생성자이므로 Spring이 @Autowired 없이도 자동으로 생성자 주입을 수행한다.
 *
 * [트랜잭션 전략]
 * 클래스 레벨: readOnly=true (기본 - 읽기 전용, 불필요한 flush 방지)
 * signUp 메서드: @Transactional 오버라이드 (쓰기 트랜잭션으로 전환)
 */
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public MemberService(MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * 회원가입
     *
     * 흐름: 이메일 중복 체크 → 비밀번호 BCrypt 해시 → Member 엔티티 생성 → DB 저장
     *
     * @throws IllegalArgumentException 이메일이 이미 존재하는 경우
     */
    @Transactional
    public void signUp(SignUpRequest request) {
        // 이메일 중복 체크 — count 쿼리보다 가벼운 existsByEmail 사용
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Kotlin Member의 기본값 파라미터는 Java에서 사용 불가 → 전체 인자 명시
        Member member = new Member(
            0L,                        // id: DB auto-increment이므로 0 전달 (JPA가 무시함)
            request.getEmail(),
            encodedPassword,
            request.getNickname(),
            request.getPhoneNumber(),  // nullable — null 허용
            request.getRole(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        memberRepository.save(member);
    }

    /**
     * 로그인
     *
     * 흐름: 이메일로 회원 조회 → BCrypt 비밀번호 검증 → JWT Access Token 발급
     *
     * @throws IllegalArgumentException 이메일 미존재 또는 비밀번호 불일치 시
     */
    public TokenResponse login(LoginRequest request) {
        // 보안 원칙: 이메일 미존재와 비밀번호 불일치 메시지를 의도적으로 구분
        // (실제 운영에서는 "이메일 또는 비밀번호를 확인하세요."로 통합하는 것이 보안상 권장됨)
        Member member = memberRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.createAccessToken(member.getEmail(), member.getRole());

        return new TokenResponse(accessToken, "Bearer");
    }
}
