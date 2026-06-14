package com.youth.food_sharing.common.exception

import com.youth.food_sharing.common.dto.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기
 *
 * @RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 이 클래스에서 통합 처리
 * 모든 에러 응답은 BaseResponse.fail()로 통일하여 클라이언트가 일관된 포맷을 수신하게 한다.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * @Valid 검증 실패 처리 (400 Bad Request)
     *
     * SignUpRequest, LoginRequest의 필드 검증 실패 시 발생.
     * 여러 필드가 동시에 실패할 수 있으므로 모든 에러를 수집하여 반환한다.
     * 예: "email: 올바른 이메일 형식이 아닙니다., password: 비밀번호는 최소 8자 이상이어야 합니다."
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<BaseResponse<Nothing>> {
        val message = e.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.badRequest().body(BaseResponse.fail(message))
    }

    /**
     * 비즈니스 규칙 위반 처리 (400 Bad Request)
     *
     * MemberService에서 발생하는 케이스:
     * - 이메일 중복 (signUp)
     * - 이메일 미존재 / 비밀번호 불일치 (login)
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<BaseResponse<Nothing>> {
        return ResponseEntity.badRequest().body(BaseResponse.fail(e.message ?: "잘못된 요청입니다."))
    }

    /**
     * 예기치 못한 서버 오류 처리 (500 Internal Server Error)
     *
     * 위 핸들러가 처리하지 못한 모든 예외의 최후 방어선.
     * 내부 에러 메시지는 클라이언트에 노출하지 않는다.
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<BaseResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(BaseResponse.fail("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
    }
}
