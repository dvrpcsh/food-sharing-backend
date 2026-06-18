package com.youth.food_sharing.post.service

import com.youth.food_sharing.member.repository.MemberRepository
import com.youth.food_sharing.post.domain.Post
import com.youth.food_sharing.post.dto.PostCreateRequest
import com.youth.food_sharing.post.dto.PostResponse
import com.youth.food_sharing.post.repository.PostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 나눔 게시글 비즈니스 로직 서비스
 *
 * [트랜잭션 전략]
 * 클래스 레벨: readOnly=true (기본 - 읽기 전용, 불필요한 flush 방지)
 * createPost 메서드: @Transactional 오버라이드 (쓰기 트랜잭션으로 전환)
 */
@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository
) {

    /**
     * 게시글 등록
     *
     * authorEmail은 컨트롤러가 SecurityContext(JwtAuthenticationFilter가 세팅한 인증 정보)에서
     * 추출한 이메일이다. 클라이언트가 요청 바디로 작성자를 임의로 지정할 수 없도록
     * 작성자 매핑은 항상 서버에서 인증 정보 기준으로 수행한다.
     *
     * @throws IllegalArgumentException 인증된 이메일에 해당하는 회원이 없는 경우
     */
    @Transactional
    fun createPost(request: PostCreateRequest, authorEmail: String): PostResponse {
        val member = memberRepository.findByEmail(authorEmail)
            .orElseThrow { IllegalArgumentException("존재하지 않는 회원입니다: $authorEmail") }

        val post = Post(
            title = request.title,
            content = request.content,
            foodName = request.foodName,
            expirationDate = request.expirationDate,
            member = member
        )

        return PostResponse.from(postRepository.save(post))
    }

    /** 전체 게시글 목록 조회 (인증 불필요) */
    fun getAllPosts(): List<PostResponse> =
        postRepository.findAll().map { PostResponse.from(it) }

    /**
     * 게시글 단건 상세 조회
     *
     * @throws IllegalArgumentException 해당 id의 게시글이 없는 경우
     */
    fun getPostById(id: Long): PostResponse {
        val post = postRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 게시글입니다: $id") }

        return PostResponse.from(post)
    }
}
