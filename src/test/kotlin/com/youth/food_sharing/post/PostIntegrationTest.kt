package com.youth.food_sharing.post

import com.fasterxml.jackson.databind.ObjectMapper
import com.youth.food_sharing.member.dto.LoginRequest
import com.youth.food_sharing.member.dto.SignUpRequest
import com.youth.food_sharing.member.repository.MemberRepository
import com.youth.food_sharing.post.dto.PostCreateRequest
import com.youth.food_sharing.post.repository.PostRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Post 도메인 통합 테스트
 *
 * @SpringBootTest로 전체 컨텍스트(컨트롤러 → 서비스 → 리포지토리 → 실제 MySQL)를 띄워
 * application.properties에 설정된 로컬 MySQL(food_sharing)에 직접 연결한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PostIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val postRepository: PostRepository
) {

    private val testEmail = "post-integration-test@example.com"

    @AfterEach
    fun cleanUp() {
        memberRepository.findByEmail(testEmail).ifPresent { member ->
            postRepository.findAll()
                .filter { it.member.id == member.id }
                .forEach { postRepository.delete(it) }
            memberRepository.delete(member)
        }
    }

    @Test
    fun 인증된_유저_게시글_등록_성공() {
        val signUpRequest = SignUpRequest(
            email = testEmail,
            password = "password123",
            nickname = "나눔이"
        )
        mockMvc.perform(
            post("/api/v1/members/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest))
        ).andExpect(status().isCreated)

        val loginResponse = mockMvc.perform(
            post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        LoginRequest(
                            email = testEmail,
                            password = "password123"
                        )
                    )
                )
        ).andExpect(status().isOk).andReturn()

        val accessToken = objectMapper.readTree(loginResponse.response.contentAsString)
            .path("data").path("accessToken").asText()

        val postCreateRequest = PostCreateRequest(
            title = "유통기한 임박 김치찌개 나눔합니다",
            content = "어제 만든 김치찌개 한 그릇 나눔합니다. 오늘 저녁까지 가져가실 분 댓글 주세요.",
            foodName = "김치찌개"
        )

        val createResponse = mockMvc.perform(
            post("/api/v1/posts")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value(postCreateRequest.title))
            .andExpect(jsonPath("$.data.authorNickname").value("나눔이"))
            .andReturn()

        val postId = objectMapper.readTree(createResponse.response.contentAsString)
            .path("data").path("id").asLong()

        val savedPost = postRepository.findById(postId).orElseThrow()
        val author = memberRepository.findByEmail(testEmail).orElseThrow()

        assertEquals(postCreateRequest.title, savedPost.title)
        assertEquals(postCreateRequest.content, savedPost.content)
        assertEquals(author.id, savedPost.member.id)
    }

    @Test
    fun 비인증_사용자_게시글_등록_실패() {
        val postCreateRequest = PostCreateRequest(
            title = "인증 없이 등록 시도",
            content = "이 요청은 거부되어야 한다."
        )

        // SecurityConfig에 AuthenticationEntryPoint를 따로 설정하지 않아 기본값인 403으로 거부된다.
        mockMvc.perform(
            post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postCreateRequest))
        ).andExpect(status().isForbidden)
    }
}
