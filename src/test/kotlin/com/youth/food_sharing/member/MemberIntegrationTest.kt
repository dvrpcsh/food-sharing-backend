package com.youth.food_sharing.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.youth.food_sharing.member.dto.LoginRequest
import com.youth.food_sharing.member.dto.SignUpRequest
import com.youth.food_sharing.member.repository.MemberRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Member 도메인 통합 테스트
 *
 * @SpringBootTest로 전체 컨텍스트(컨트롤러 → 서비스 → 리포지토리 → 실제 MySQL)를 띄워
 * application.properties에 설정된 로컬 MySQL(food_sharing)에 직접 연결한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberIntegrationTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val memberRepository: MemberRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    private val testEmail = "integration-test@example.com"

    @AfterEach
    fun cleanUp() {
        memberRepository.findByEmail(testEmail).ifPresent {
            memberRepository.delete(it)
        }
    }

    @Test
    fun 회원가입_성공() {
        val request = SignUpRequest(
            email = testEmail,
            password = "password123",
            nickname = "테스트유저"
        )

        mockMvc.post("/api/v1/members/signup", request)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))

        val saved = memberRepository.findByEmail(testEmail).orElseThrow()
        assertNotEquals(request.password, saved.password)
        assertTrue(passwordEncoder.matches(request.password, saved.password))
    }

    @Test
    fun 회원가입_실패_이메일중복() {
        val request = SignUpRequest(
            email = testEmail,
            password = "password123",
            nickname = "테스트유저"
        )

        mockMvc.post("/api/v1/members/signup", request)
            .andExpect(status().isCreated)

        mockMvc.post("/api/v1/members/signup", request)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다: $testEmail"))
    }

    @Test
    fun 로그인_성공_JWT_발급() {
        val signUpRequest = SignUpRequest(
            email = testEmail,
            password = "password123",
            nickname = "테스트유저"
        )
        mockMvc.post("/api/v1/members/signup", signUpRequest)
            .andExpect(status().isCreated)

        val loginRequest = LoginRequest(
            email = testEmail,
            password = "password123"
        )

        mockMvc.post("/api/v1/members/login", loginRequest)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty)
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andDo {
                val token = objectMapper.readTree(it.response.contentAsString)
                    .path("data").path("accessToken").asText()
                assertFalse(token.isBlank())
            }
    }

    private fun MockMvc.post(url: String, body: Any) =
        perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
}
