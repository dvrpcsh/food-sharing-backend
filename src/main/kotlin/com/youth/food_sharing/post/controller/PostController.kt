package com.youth.food_sharing.post.controller

import com.youth.food_sharing.common.dto.BaseResponse
import com.youth.food_sharing.post.dto.PostCreateRequest
import com.youth.food_sharing.post.dto.PostResponse
import com.youth.food_sharing.post.service.PostService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 나눔 게시글 API 컨트롤러
 *
 * Base URL: /api/v1/posts
 * - 예외 처리는 GlobalExceptionHandler에 위임 — 컨트롤러는 Happy Path만 담당
 */
@RestController
@RequestMapping("/api/v1/posts")
class PostController(
    private val postService: PostService
) {

    /**
     * POST /api/v1/posts
     *
     * 인증 필요. JwtAuthenticationFilter가 SecurityContext에 세팅한 인증 정보(이메일)를
     * 그대로 서비스에 전달하여 작성자를 매핑한다.
     */
    @PostMapping
    fun createPost(
        @Valid @RequestBody request: PostCreateRequest,
        authentication: Authentication
    ): ResponseEntity<BaseResponse<PostResponse>> {
        val response = postService.createPost(request, authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(response, "게시글이 등록되었습니다."))
    }

    /**
     * GET /api/v1/posts
     *
     * 인증 불필요 — 전체 게시글 목록 조회
     */
    @GetMapping
    fun getAllPosts(): ResponseEntity<BaseResponse<List<PostResponse>>> {
        val responses = postService.getAllPosts()
        return ResponseEntity.ok(BaseResponse.ok(responses, "게시글 목록 조회에 성공했습니다."))
    }
}
