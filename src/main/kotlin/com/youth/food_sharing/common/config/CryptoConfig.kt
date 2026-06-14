package com.youth.food_sharing.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * 암호화 관련 Bean 설정
 *
 * Spring Security 전체를 도입하지 않고 BCryptPasswordEncoder만 독립적으로 사용한다.
 * Spring Security 연동 시 SecurityConfig로 이전하거나 통합할 것.
 */
@Configuration
class CryptoConfig {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}
