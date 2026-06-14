package com.youth.food_sharing.member.repository

import com.youth.food_sharing.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * Member JPA 리포지토리
 *
 * Spring Data JPA가 메서드 이름 기반으로 쿼리를 자동 생성한다.
 * - findByEmail    : 로그인 및 이메일 중복 조회 시 사용
 * - existsByEmail  : 회원가입 시 이메일 중복 여부 단순 확인 (count 쿼리보다 가볍다)
 *
 * 복잡한 동적 쿼리가 필요해질 경우 QueryDSL 또는 @Query를 이 인터페이스에 추가한다.
 */
interface MemberRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: String): Optional<Member>

    fun existsByEmail(email: String): Boolean
}
