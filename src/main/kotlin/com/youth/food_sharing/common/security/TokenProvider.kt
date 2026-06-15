package com.youth.food_sharing.common.security

import com.youth.food_sharing.member.domain.Role
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT Access Token 발급 담당
 *
 * application.properties의 jwt.secret / jwt.expiration-period를 주입받아
 * 로그인 성공 시 email, role을 Claim에 담은 Access Token을 생성한다.
 */
@Component
class TokenProvider(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.expiration-period}") private val expirationPeriod: Long
) {

    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun createAccessToken(email: String, role: Role): String {
        val now = Date()
        val expiry = Date(now.time + expirationPeriod)

        return Jwts.builder()
            .subject(email)
            .claim("email", email)
            .claim("role", role.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(secretKey)
            .compact()
    }
}
