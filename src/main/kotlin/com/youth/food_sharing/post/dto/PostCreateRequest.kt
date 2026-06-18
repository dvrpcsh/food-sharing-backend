package com.youth.food_sharing.post.dto

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

/**
 * 나눔 게시글 등록 요청 DTO
 *
 * 작성자(member)는 요청 바디로 받지 않고, 인증된 SecurityContext의 회원 정보를
 * 서비스 레이어에서 매핑한다 (클라이언트가 임의로 작성자를 조작하는 것을 방지).
 */
data class PostCreateRequest(

    @field:NotBlank(message = "제목은 필수입니다.")
    val title: String,

    @field:NotBlank(message = "내용은 필수입니다.")
    val content: String,

    val foodName: String? = null,

    val expirationDate: LocalDateTime? = null
)
