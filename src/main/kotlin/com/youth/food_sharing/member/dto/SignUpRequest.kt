package com.youth.food_sharing.member.dto

import com.youth.food_sharing.member.domain.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * 회원가입 요청 DTO
 *
 * - Bean Validation 어노테이션은 Kotlin data class 프로퍼티에 적용 시
 *   반드시 @field: 접두사를 붙여야 생성자 파라미터가 아닌 필드에 위임된다.
 * - 컨트롤러에서 @Valid와 함께 사용하면 유효성 검증 실패 시
 *   MethodArgumentNotValidException이 발생 → GlobalExceptionHandler에서 처리 예정
 */
data class SignUpRequest(

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    val password: String,

    @field:NotBlank(message = "닉네임은 필수입니다.")
    @field:Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다.")
    val nickname: String,

    /** 선택 입력 — 전화번호 형식: 010-1234-5678 또는 01012345678 */
    @field:Pattern(
        regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
        message = "올바른 전화번호 형식이 아닙니다."
    )
    val phoneNumber: String? = null,

    /** 기본값은 자취 청년(YOUTH_CUSTOMER); 나눔 제공자로 가입 시 PROVIDER 전달 */
    val role: Role = Role.YOUTH_CUSTOMER
)
