package com.youth.food_sharing.member.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 로그인 요청 DTO
 *
 * 로그인 성공 시 서비스 레이어에서 JWT 토큰을 생성하여 반환할 예정.
 * Spring Security 도입 전까지는 수동 이메일/비밀번호 검증 방식으로 구현한다.
 */
data class LoginRequest(

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    val password: String
)
