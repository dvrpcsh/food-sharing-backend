package com.youth.food_sharing.common.init

import com.youth.food_sharing.member.domain.Member
import com.youth.food_sharing.member.repository.MemberRepository
import com.youth.food_sharing.post.domain.Post
import com.youth.food_sharing.post.domain.PostStatus
import com.youth.food_sharing.post.repository.PostRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 로컬 개발 / React Native 프론트엔드 연동 테스트용 초기 데이터 적재
 *
 * 서버 기동 시 테스트 회원 2명과 나눔 게시글을 자동 생성한다.
 * 이미 해당 이메일의 회원이 존재하면 그 회원의 시드 생성을 건너뛰어
 * 서버를 재시작해도 데이터가 중복으로 쌓이지 않는다.
 */
@Component
class DataInitializer(
    private val memberRepository: MemberRepository,
    private val postRepository: PostRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        seedMemberWithPosts(
            email = "user1@test.com",
            nickname = "테스트유저1",
            posts = listOf(
                SeedPost("엄마표 겉절이 나눠요", "어제 담은 겉절이가 많이 남아서 나눔합니다. 오늘 저녁까지 가져가실 분 연락주세요.", "겉절이"),
                SeedPost("자취방 남은 스팸 가져가세요", "스팸 캔이 너무 많아서 두 개 나눔합니다. 가까운 분 환영이에요.", "스팸")
            )
        )

        seedMemberWithPosts(
            email = "user2@test.com",
            nickname = "테스트유저2",
            posts = listOf(
                SeedPost("유통기한 임박 우유 나눔", "오늘 안에 가져가실 분만 연락주세요. 1L짜리 흰우유입니다.", "흰우유"),
                SeedPost("혼자 먹기 많은 김장김치 나눔", "올해 김장한 김치가 많아서 한 통 나눔합니다.", "김장김치")
            )
        )
    }

    private fun seedMemberWithPosts(email: String, nickname: String, posts: List<SeedPost>) {
        if (memberRepository.existsByEmail(email)) {
            return
        }

        val member = memberRepository.save(
            Member(
                email = email,
                password = passwordEncoder.encode("password123"),
                nickname = nickname
            )
        )

        posts.forEach { seed ->
            postRepository.save(
                Post(
                    title = seed.title,
                    content = seed.content,
                    foodName = seed.foodName,
                    expirationDate = LocalDateTime.now().plusDays(3),
                    status = PostStatus.IN_PROGRESS,
                    member = member
                )
            )
        }
    }

    private data class SeedPost(val title: String, val content: String, val foodName: String)
}
