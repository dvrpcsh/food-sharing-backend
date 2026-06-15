package com.youth.food_sharing.member.controller

import com.youth.food_sharing.common.dto.BaseResponse
import com.youth.food_sharing.member.dto.LoginRequest
import com.youth.food_sharing.member.dto.SignUpRequest
import com.youth.food_sharing.member.dto.TokenResponse
import com.youth.food_sharing.member.service.MemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 회원 API 컨트롤러
 *
 * Base URL: /api/v1/members
 * - Java로 작성된 MemberService를 Kotlin 생성자 주입으로 연결
 * - 예외 처리는 GlobalExceptionHandler에 위임 — 컨트롤러는 Happy Path만 담당
 */
@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    /**
     * POST /api/v1/members/signup
     *
     * 요청 흐름:
     *   [SignUpRequest @Valid 검증] → MemberService.signUp() → 201 Created
     * 검증 실패 또는 이메일 중복 시 GlobalExceptionHandler가 400 Bad Request 반환
     */
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest
    ): ResponseEntity<BaseResponse<Nothing>> {
        memberService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok("회원가입이 완료되었습니다."))
    }

    /**
     * POST /api/v1/members/login
     *
     * 요청 흐름:
     *   [LoginRequest @Valid 검증] → MemberService.login() → 200 OK + TokenResponse
     * 이메일 미존재 또는 비밀번호 불일치 시 GlobalExceptionHandler가 400 Bad Request 반환
     */
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<BaseResponse<TokenResponse>> {
        val tokenResponse = memberService.login(request)
        return ResponseEntity.ok(BaseResponse.ok(tokenResponse, "로그인에 성공했습니다."))
    }
}
