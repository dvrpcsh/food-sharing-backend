package com.youth.food_sharing.member.domain

/**
 * 회원 역할(권한) 구분
 *
 * YOUTH_CUSTOMER : 자취 청년 — 나눔 음식을 받는 주요 사용자
 * PROVIDER       : 나눔 제공자 — 음식을 올리고 나눠주는 사용자
 * ADMIN          : 서비스 관리자 — 전체 관리 권한 보유
 *
 * DB 컬럼에는 EnumType.STRING으로 저장하여 순서 변경에 안전하게 유지한다.
 */
enum class Role {
    YOUTH_CUSTOMER,
    PROVIDER,
    ADMIN
}
