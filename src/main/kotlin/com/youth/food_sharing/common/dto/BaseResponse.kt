package com.youth.food_sharing.common.dto

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 모든 API 응답을 감싸는 공통 포맷
 * - success: 요청 처리 성공 여부
 * - message: 클라이언트에 표시할 사람 친화적 메시지
 * - data: 실제 응답 페이로드 (실패 시 null, @JsonInclude로 직렬화에서 제외)
 *
 * React Native(앱)와 Next.js(웹) 모두 이 포맷을 기대하므로
 * 모든 컨트롤러는 ResponseEntity<BaseResponse<T>> 형태로 반환한다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) {
    companion object {
        /** 페이로드가 있는 성공 응답 */
        fun <T> ok(data: T, message: String = "요청이 성공적으로 처리되었습니다."): BaseResponse<T> =
            BaseResponse(success = true, message = message, data = data)

        /** 페이로드 없는 성공 응답 (가입 완료, 삭제 완료 등) */
        fun ok(message: String = "요청이 성공적으로 처리되었습니다."): BaseResponse<Nothing> =
            BaseResponse(success = true, message = message)

        /** 실패 응답 — GlobalExceptionHandler에서 주로 사용 */
        fun fail(message: String): BaseResponse<Nothing> =
            BaseResponse(success = false, message = message)
    }
}
