package com.youth.food_sharing.member.dto

/**
 * 로그인 성공 응답 DTO
 *
 * MemberService.login()에서 발급한 JWT Access Token을 담아 클라이언트에 반환한다.
 */
data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
