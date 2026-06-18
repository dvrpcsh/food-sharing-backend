package com.youth.food_sharing.post.dto

import com.youth.food_sharing.post.domain.Post
import com.youth.food_sharing.post.domain.PostStatus
import java.time.LocalDateTime

/**
 * 나눔 게시글 응답 DTO
 *
 * Post.member는 LAZY 연관관계이므로 엔티티를 그대로 직렬화하지 않고,
 * 트랜잭션이 열려 있는 서비스 레이어에서 필요한 필드만 꺼내 DTO로 변환한다.
 */
data class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val foodName: String?,
    val expirationDate: LocalDateTime?,
    val status: PostStatus,
    val authorId: Long,
    val authorNickname: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(post: Post): PostResponse = PostResponse(
            id = post.id,
            title = post.title,
            content = post.content,
            foodName = post.foodName,
            expirationDate = post.expirationDate,
            status = post.status,
            authorId = post.member.id,
            authorNickname = post.member.nickname,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
    }
}
