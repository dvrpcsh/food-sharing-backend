package com.youth.food_sharing.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private const val AUTHORIZATION_HEADER = "Authorization"
private const val BEARER_PREFIX = "Bearer "

/**
 * JWT 인증 필터
 *
 * 모든 요청에서 `Authorization: Bearer <token>` 헤더를 추출하여
 * TokenProvider로 유효성을 검증하고, 유효한 토큰이면 email/role 정보를 바탕으로
 * SecurityContextHolder에 인증 정보를 세팅한다.
 *
 * 토큰이 없거나 유효하지 않으면 인증 정보를 세팅하지 않고 다음 필터로 통과시킨다.
 * (인증이 필요한 API는 SecurityConfig의 authorizeHttpRequests에서 차단된다.)
 */
@Component
class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null && tokenProvider.validateToken(token)) {
            val email = tokenProvider.getEmail(token)
            val role = tokenProvider.getRole(token)

            val authentication = UsernamePasswordAuthenticationToken(
                email,
                null,
                listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
            )
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
}
