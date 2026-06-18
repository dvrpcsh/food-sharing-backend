package com.youth.food_sharing.post.domain

import com.youth.food_sharing.member.domain.Member
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 나눔 게시글 엔티티
 *
 * [설계 원칙]
 * - id를 val로 선언해 불변성 유지; 작성자(member)는 변경 불가능한 값이므로 val
 * - member 연관관계는 FetchType.LAZY로 N+1 문제를 회피 (목록 조회 시 불필요한 join 방지)
 * - createdAt은 updatable=false로 최초 생성 후 변경 불가 (Member 엔티티와 동일한 컨벤션)
 */
@Entity
@Table(name = "posts")
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(length = 50)
    var foodName: String? = null,

    var expirationDate: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PostStatus = PostStatus.IN_PROGRESS,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
