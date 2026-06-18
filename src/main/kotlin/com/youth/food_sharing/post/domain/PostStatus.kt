package com.youth.food_sharing.post.domain

/**
 * 나눔 게시글 진행 상태
 *
 * IN_PROGRESS : 나눔중 — 아직 수령 가능한 상태
 * COMPLETED   : 나눔완료 — 모든 수량이 나눔 완료된 상태
 *
 * DB 컬럼에는 EnumType.STRING으로 저장하여 순서 변경에 안전하게 유지한다.
 */
enum class PostStatus {
    IN_PROGRESS,
    COMPLETED
}
