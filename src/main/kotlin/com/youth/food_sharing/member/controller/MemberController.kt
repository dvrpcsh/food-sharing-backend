package com.youth.food_sharing.member.controller

import com.youth.food_sharing.common.dto.BaseResponse
import com.youth.food_sharing.member.dto.LoginRequest
import com.youth.food_sharing.member.dto.SignUpRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 회원 관련 API 컨트롤러
 *
 * Base URL: /api/v1/members
 * - 버전 prefix(/v1)를 붙여 향후 Breaking Change 발생 시 /v2로 무중단 전환 가능
 * - React Native(앱)와 Next.js(웹) 모두 동일한 엔드포인트를 호출
 *
 * [현재 상태] 하네스(틀) 단계 — 실제 비즈니스 로직은 MemberService 구현 후 위임
 */
@RestController
@RequestMapping("/api/v1/members")
class MemberController {

    /**
     * POST /api/v1/members/signup
     *
     * 요청 흐름:
     *   클라이언트 → [SignUpRequest 유효성 검증] → MemberService.signUp() → DB 저장 → BaseResponse 반환
     *
     * @Valid: Bean Validation 트리거 — 실패 시 400 Bad Request + 에러 메시지 반환 (GlobalExceptionHandler 처리)
     */
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest
    ): ResponseEntity<BaseResponse<Nothing>> {
        // TODO: MemberService.signUp(request) 주입 후 호출
        return ResponseEntity.ok(BaseResponse.ok("회원가입이 완료되었습니다."))
    }

    /**
     * POST /api/v1/members/login
     *
     * 요청 흐름:
     *   클라이언트 → [LoginRequest 유효성 검증] → MemberService.login() → JWT 발급 → BaseResponse<TokenResponse> 반환
     *
     * JWT 도입 전까지는 임시 성공 응답을 반환하는 틀로 유지한다.
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<BaseResponse<Nothing>> {
        // TODO: MemberService.login(request) 주입 후 호출 (TokenResponse 반환)
        return ResponseEntity.ok(BaseResponse.ok("로그인에 성공했습니다."))
    }
}
