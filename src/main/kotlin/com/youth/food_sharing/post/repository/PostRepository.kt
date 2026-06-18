package com.youth.food_sharing.post.repository

import com.youth.food_sharing.post.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Post JPA 리포지토리
 *
 * 전체 조회(findAll)와 단건 조회(findById)는 JpaRepository가 기본 제공한다.
 * 페이징/검색 조건이 추가되면 이 인터페이스에 메서드를 확장한다.
 */
interface PostRepository : JpaRepository<Post, Long>
