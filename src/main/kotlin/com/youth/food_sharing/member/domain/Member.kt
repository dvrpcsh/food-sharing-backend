package com.youth.food_sharing.member.domain

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 회원 엔티티
 *
 * [Kotlin JPA 플러그인 동작 방식]
 * - kotlin("plugin.jpa") → @Entity 클래스에 no-arg 생성자 자동 추가 (JPA 스펙 필수)
 * - allOpen { annotation("jakarta.persistence.Entity") } → 클래스를 open으로 열어
 *   Hibernate 프록시(지연 로딩용 서브클래스) 생성을 허용
 *
 * [설계 원칙]
 * - id를 val로 선언해 불변성 유지; 나머지 변경 가능 필드는 var
 * - 비밀번호는 서비스 레이어에서 해시 후 저장 (평문 저장 금지)
 * - createdAt은 updatable=false로 최초 생성 후 변경 불가
 */
@Entity
@Table(
    name = "members",
    uniqueConstraints = [UniqueConstraint(name = "uk_member_email", columnNames = ["email"])]
)
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 100)
    var email: String,

    /** 반드시 BCrypt 등으로 해시된 값만 저장할 것 */
    @Column(nullable = false)
    var password: String,

    @Column(nullable = false, length = 30)
    var nickname: String,

    @Column(length = 20)
    var phoneNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var role: Role = Role.YOUTH_CUSTOMER,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
