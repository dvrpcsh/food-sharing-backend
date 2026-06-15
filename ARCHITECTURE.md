# Food Sharing Backend — Architecture

> 청년 자취생 식사 나눔 서비스 백엔드 아키텍처 문서  
> **규칙:** 큰 기능(도메인) 하나가 완성될 때마다 해당 섹션을 이 문서에 추가한다.

---

## Tech Stack

| 항목 | 내용 |
|---|---|
| Framework | Spring Boot 3.5.x |
| Language | Kotlin 1.9.x (Main) + Java 21 (Service Layer) |
| Database | MySQL 8.x |
| ORM | Spring Data JPA (Hibernate) |
| 빌드 도구 | Gradle (Kotlin DSL) |
| API 스펙 | RESTful / JSON |
| 공통 응답 | `BaseResponse<T>` |

---

## 패키지 구조 원칙

```
src/
├── main/
│   ├── kotlin/com/youth/food_sharing/
│   │   ├── {domain}/
│   │   │   ├── controller/   ← Kotlin (REST 엔드포인트, Happy Path만)
│   │   │   ├── domain/       ← Kotlin (JPA Entity, Enum)
│   │   │   ├── dto/          ← Kotlin (Request/Response data class)
│   │   │   └── repository/   ← Kotlin (JPA Repository interface)
│   │   └── common/
│   │       ├── config/       ← Kotlin (Bean 설정)
│   │       ├── dto/          ← Kotlin (BaseResponse 공통 포맷)
│   │       └── exception/    ← Kotlin (GlobalExceptionHandler)
│   └── java/com/youth/food_sharing/
│       └── {domain}/
│           └── service/      ← Java (복잡한 비즈니스 로직)
└── test/
    └── kotlin/...
```

> **하이브리드 전략:** Entity/DTO/Repository/Controller는 Kotlin, 핵심 Service 로직은 Java로 작성한다.  
> Lombok 사용 금지 — Java는 순수 POJO 생성자, Kotlin은 `data class` 활용.

---

## .gitignore — 저장소에 올라가지 않는 파일 목록

> **중요: 다른 환경에서 작업 시 아래 파일/디렉토리는 Git에 없으므로 USB 등 별도 수단으로 이동해야 한다.**

### 빌드 & 캐시 (자동 재생성 가능 — USB 불필요)

| 경로 | 설명 | USB 필요 여부 |
|---|---|---|
| `build/` | Gradle 빌드 산출물 (`.class`, `.jar` 등) | ❌ `./gradlew build`로 재생성 |
| `.gradle/` | Gradle 의존성 캐시 | ❌ 빌드 시 자동 다운로드 |
| `.kotlin/` | Kotlin 컴파일러 캐시 | ❌ 컴파일 시 자동 재생성 |
| `bin/` | 컴파일된 클래스 파일 | ❌ 자동 재생성 |
| `out/` | IntelliJ 빌드 출력 | ❌ 자동 재생성 |

### IDE 설정 (환경마다 다름 — USB 이동 비권장)

| 경로 | 설명 | USB 필요 여부 |
|---|---|---|
| `.idea/` | IntelliJ IDEA 프로젝트 설정 | ⚠️ 환경마다 달라지므로 이동 불필요. 새 환경에서 IntelliJ로 열면 자동 생성 |
| `*.iws`, `*.iml`, `*.ipr` | IntelliJ 워크스페이스/모듈 파일 | ⚠️ 동일 |
| `.vscode/` | VS Code 설정 | ⚠️ 동일 |
| `.classpath`, `.project` | Eclipse/STS 설정 | ⚠️ 동일 |
| `.sts4-cache/` | Spring Tool Suite 캐시 | ⚠️ 동일 |

### 문서

| 경로 | 설명 | USB 필요 여부 |
|---|---|---|
| `HELP.md` | Spring Initializr 자동 생성 안내 문서 | ❌ 불필요 |

---

### ⚠️ USB에 반드시 담아야 할 파일

현재 프로젝트에서 Git에 올라가지 않으면서 **개발 환경마다 직접 세팅해야 하는** 파일은 아래와 같다.

| 파일 | 위치 | 내용 | 비고 |
|---|---|---|---|
| `application.properties` | `src/main/resources/` | **현재 Git에 포함됨** — 단, `spring.datasource.password` 값은 비어 있으므로 새 환경에서 직접 채워야 함 | 비밀번호 등 민감값은 절대 Git에 올리지 말 것 |

> **향후 민감 설정 분리 권장:** `application-local.properties` (gitignore 추가) 파일을 별도로 만들어 DB 비밀번호 등 민감값을 분리하고, USB로 관리하는 패턴을 권장한다.

```
# application-local.properties (Git 제외 예정)
spring.datasource.password=실제비밀번호
```

---

## 새 환경에서 프로젝트 시작하는 방법

```bash
# 1. 저장소 클론
git clone https://github.com/dvrpcsh/food-sharing-backend.git

# 2. MySQL DB 생성
CREATE DATABASE food_sharing CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. application.properties에 DB 비밀번호 입력
# src/main/resources/application.properties
# spring.datasource.password= 에 비밀번호 입력

# 4. 빌드 및 실행
./gradlew bootRun
```

---

---

## Domain: Member (회원)

> 완성일: 2026-06-14  
> 커밋 범위: `82c71af` → `c5bce88`

### 패키지 구조

```
com/youth/food_sharing/
│
├── common/
│   ├── config/
│   │   ├── CryptoConfig.kt              ← BCryptPasswordEncoder @Bean (Kotlin)
│   │   └── SecurityConfig.java          ← SecurityFilterChain 설정 (Java)
│   ├── dto/
│   │   └── BaseResponse.kt              ← 공통 응답 포맷 (제네릭)
│   ├── exception/
│   │   └── GlobalExceptionHandler.kt    ← 전역 예외 처리기
│   └── security/
│       ├── TokenProvider.kt             ← JWT Access Token 발급·검증 (Kotlin)
│       └── JwtAuthenticationFilter.kt   ← JWT 인증 필터 (Kotlin, OncePerRequestFilter)
│
└── member/
    ├── controller/
    │   └── MemberController.kt          ← 회원 REST 컨트롤러 (Kotlin)
    ├── domain/
    │   ├── Member.kt                    ← JPA 엔티티 (Kotlin class)
    │   └── Role.kt                      ← 권한 Enum
    ├── dto/
    │   ├── SignUpRequest.kt             ← 회원가입 요청 DTO
    │   ├── LoginRequest.kt              ← 로그인 요청 DTO
    │   └── TokenResponse.kt             ← 로그인 성공 응답 DTO (JWT)
    ├── repository/
    │   └── MemberRepository.kt          ← JPA Repository (Kotlin interface)
    └── service/
        └── MemberService.java           ← 비즈니스 로직 (Java)
```

### 핵심 클래스 역할

| 클래스 | 언어 | 역할 |
|---|---|---|
| `BaseResponse<T>` | Kotlin | 모든 API 응답 래퍼. `ok(data)` / `ok(message)` / `fail(message)` 팩토리 메서드 제공 |
| `GlobalExceptionHandler` | Kotlin | `@Valid` 실패(400), `IllegalArgumentException`(400), 미처리 예외(500)를 `BaseResponse.fail()`로 통일 |
| `CryptoConfig` | Kotlin | `BCryptPasswordEncoder`를 Spring Bean으로 등록 |
| `SecurityConfig` | **Java** | `SecurityFilterChain` 정의. CSRF 비활성화 + STATELESS, `/signup`·`/login`(POST)은 `permitAll()`, 나머지는 `authenticated()`. `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 등록 |
| `JwtAuthenticationFilter` | Kotlin | `OncePerRequestFilter` 구현. `Authorization: Bearer <token>` 추출 → `TokenProvider.validateToken()` → 유효하면 email/role로 `UsernamePasswordAuthenticationToken`을 만들어 `SecurityContextHolder`에 세팅 |
| `Member` | Kotlin | `members` 테이블 매핑 JPA 엔티티. `plugin.jpa`(no-arg) + `allOpen`(@Entity) 플러그인 적용 |
| `Role` | Kotlin | `YOUTH_CUSTOMER` / `PROVIDER` / `ADMIN` — `EnumType.STRING`으로 DB 저장 |
| `SignUpRequest` | Kotlin | Bean Validation 적용 (`@field:` 접두사 필수). 이메일·비밀번호·닉네임 필수, 전화번호 선택 |
| `LoginRequest` | Kotlin | 이메일 + 비밀번호 검증 |
| `MemberRepository` | Kotlin | `findByEmail()`, `existsByEmail()` 쿼리 메서드 제공 |
| `TokenProvider` | Kotlin | `jwt.secret` / `jwt.expiration-period`를 주입받아 HS256 JWT Access Token 발급. Claim에 `email`, `role` 포함 |
| `TokenResponse` | Kotlin | 로그인 성공 응답 DTO. `accessToken`, `tokenType`(기본값 `Bearer`) |
| `MemberService` | **Java** | 단일 생성자 주입(Lombok 없음). `signUp`: 중복 체크 → BCrypt 해시 → 저장. `login`: 조회 → 비밀번호 검증 → `TokenProvider`로 JWT 발급 후 `TokenResponse` 반환 |
| `MemberController` | Kotlin | Java `MemberService` 생성자 주입. Happy Path만 담당, 예외는 Handler에 위임 |

### ERD — `members` 테이블 필드 스펙

| 필드 (컬럼명) | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | `BIGINT` | PK, AUTO_INCREMENT | 식별자 |
| `email` | `VARCHAR(100)` | UNIQUE(`uk_member_email`), NOT NULL | 로그인 키 |
| `password` | `VARCHAR(255)` | NOT NULL | BCrypt 해시값 저장 (평문 저장 금지) |
| `nickname` | `VARCHAR(30)` | NOT NULL | 표시 이름 |
| `phone_number` | `VARCHAR(20)` | NULL 허용 | 선택 입력, `010-1234-5678` 또는 `01012345678` 형식 |
| `role` | `VARCHAR(20)` | NOT NULL, STRING | `YOUTH_CUSTOMER` / `PROVIDER` / `ADMIN`, 기본값 `YOUTH_CUSTOMER` |
| `created_at` | `DATETIME` | NOT NULL, updatable=false | 가입일 |
| `updated_at` | `DATETIME` | NOT NULL | 최종 수정일 |

> 테이블/컬럼은 `spring.jpa.hibernate.ddl-auto=update` 설정에 의해 Hibernate가 엔티티 정의로부터 자동 생성·갱신한다.

### API 엔드포인트

| 메서드 | URL | 요청 Body | 성공 응답 | 실패 응답 |
|---|---|---|---|---|
| `POST` | `/api/v1/members/signup` | `SignUpRequest` | `201 { success: true, message: "회원가입이 완료되었습니다." }` | `400 { success: false, message: "이미 사용 중인 이메일..." }` |
| `POST` | `/api/v1/members/login` | `LoginRequest` | `200 { success: true, message: "로그인에 성공했습니다.", data: { accessToken, tokenType: "Bearer" } }` | `400 { success: false, message: "비밀번호가 일치하지 않습니다." }` |
| `GET` | `/api/v1/members/me` | - (`Authorization: Bearer <token>` 헤더 필요) | `200 { success: true, message: "내 정보 조회에 성공했습니다.", data: "<email>" }` | `401/403` (토큰 없음·무효 시 Spring Security가 처리) |

#### SignUpRequest 필드 검증 규칙

| 필드 | 필수 | 규칙 |
|---|---|---|
| `email` | ✅ | 이메일 형식 |
| `password` | ✅ | 최소 8자 |
| `nickname` | ✅ | 2~20자 |
| `phoneNumber` | ❌ | `010-1234-5678` 또는 `01012345678` 형식 |
| `role` | ❌ | 기본값 `YOUTH_CUSTOMER` |

### 에러 응답 흐름

```
클라이언트 요청
    │
    ├─ @Valid 실패 → MethodArgumentNotValidException
    │                        │
    ├─ 이메일 중복/불일치 → IllegalArgumentException
    │                        │
    └─ 예기치 못한 오류 → Exception
                             │
                    GlobalExceptionHandler
                             │
                    BaseResponse.fail(message)
                             │
                    { success: false, message: "..." }
```

---

### 회원가입 요청 흐름 (성공 케이스)

```
클라이언트
    │  POST /api/v1/members/signup (SignUpRequest)
    ▼
MemberController.signUp()
    │  @Valid 통과
    ▼
MemberService.signUp()  [@Transactional]
    │  1) memberRepository.existsByEmail()  → 중복이면 IllegalArgumentException
    │  2) passwordEncoder.encode(password)  → BCrypt 해시
    │  3) new Member(...)
    │  4) memberRepository.save(member)     → MySQL members 테이블 INSERT
    ▼
201 Created  { success: true, message: "회원가입이 완료되었습니다." }
```

### 통합 테스트 — `MemberIntegrationTest`

- 위치: `src/test/kotlin/com/youth/food_sharing/member/MemberIntegrationTest.kt`
- `@SpringBootTest` + `@AutoConfigureMockMvc`로 컨트롤러 → 서비스 → 리포지토리 → **로컬 MySQL(`food_sharing`)** 전체 계층을 실제로 띄워 검증한다 (H2 등 임베디드 DB 미사용).
- 테스트 케이스
  - `회원가입_성공()`: 회원가입 요청 시 201 응답 + DB에 Member 저장 + 비밀번호가 BCrypt로 암호화되어 저장되는지 확인
  - `회원가입_실패_이메일중복()`: 동일 이메일로 재가입 시 `GlobalExceptionHandler`를 통해 400 + 실패 메시지 반환 확인
  - `로그인_성공_JWT_발급()`: 가입된 계정으로 로그인 시 200 응답 + `data.accessToken`/`data.tokenType`("Bearer")이 비어있지 않은 값으로 반환되는지 확인
  - `인증된_유저_내정보_조회_성공()`: 가입 → 로그인으로 발급받은 Access Token을 `Authorization: Bearer <token>` 헤더에 담아 `GET /api/v1/members/me` 호출 시 200 응답 + `data`에 본인 이메일이 반환되는지 확인
- `@AfterEach`에서 테스트용 이메일(`integration-test@example.com`) 데이터를 삭제하여 반복 실행이 가능하도록 정리한다.

---

## JWT 인증 흐름

### 개요

- 로그인 성공 시 `MemberService.login()`이 `TokenProvider`를 통해 JWT Access Token을 발급하고, `TokenResponse`에 담아 `MemberController`가 `BaseResponse.data`로 반환한다.
- 이후 클라이언트는 발급받은 Access Token을 `Authorization: Bearer <token>` 헤더에 담아 요청하며, `JwtAuthenticationFilter`가 매 요청마다 토큰을 검증해 `SecurityContext`에 인증 정보를 세팅한다.
- Spring Security(`spring-boot-starter-security`)가 적용되어 `SecurityConfig`의 `SecurityFilterChain`이 인증/인가 규칙을 담당한다. CSRF 비활성화 + `STATELESS` 세션 정책으로 REST API에 맞춘 구성이다.
- Refresh Token 발급/재발급은 아직 구현되지 않았다 (추후 도입 예정).

### TokenProvider 설정값 (`application.properties`)

| 키 | 설명 |
|---|---|
| `jwt.secret` | HS256 서명용 비밀키. `Keys.hmacShaKeyFor()`로 `SecretKey` 생성 (운영 환경은 환경변수로 오버라이드 권장) |
| `jwt.expiration-period` | Access Token 유효 기간 (ms). 현재 `3600000`(1시간) |

### TokenProvider 메서드

| 메서드 | 역할 |
|---|---|
| `createAccessToken(email, role)` | Claims에 `email`, `role`을 담아 HS256 서명된 Access Token 생성 |
| `validateToken(token)` | 서명/만료 여부 검증 (`JwtException`/`IllegalArgumentException` 시 `false`) |
| `getEmail(token)` | 토큰의 `email` claim 추출 |
| `getRole(token)` | 토큰의 `role` claim을 `Role` enum으로 변환 |

### JWT Claims 구조

| Claim | 값 | 설명 |
|---|---|---|
| `sub` (subject) | `member.email` | 토큰 주체 |
| `email` | `member.email` | 사용자 이메일 |
| `role` | `member.role.name` (`Role` enum의 문자열) | 사용자 권한 — `YOUTH_CUSTOMER` / `PROVIDER` / `ADMIN` |
| `iat` | 토큰 발급 시각 | |
| `exp` | `iat + jwt.expiration-period` | 만료 시각 |

### 로그인 → JWT 발급 흐름

```
클라이언트
    │  POST /api/v1/members/login (LoginRequest)
    ▼
MemberController.login()
    │  @Valid 통과
    ▼
MemberService.login()  [readOnly 트랜잭션]
    │  1) memberRepository.findByEmail()     → 미존재 시 IllegalArgumentException
    │  2) passwordEncoder.matches()          → 불일치 시 IllegalArgumentException
    │  3) tokenProvider.createAccessToken(email, role)
    │       └─ Jwts.builder()
    │            .subject(email)
    │            .claim("email", email)
    │            .claim("role", role.name)
    │            .issuedAt(now) / .expiration(now + expirationPeriod)
    │            .signWith(secretKey)  // HS256
    │  4) new TokenResponse(accessToken, "Bearer")
    ▼
200 OK
{
  success: true,
  message: "로그인에 성공했습니다.",
  data: { accessToken: "<JWT>", tokenType: "Bearer" }
}
```

### 관련 클래스

| 클래스 | 언어 | 역할 |
|---|---|---|
| `TokenProvider` | Kotlin | `jjwt` 라이브러리로 HS256 Access Token 생성·검증 (`common/security`) |
| `TokenResponse` | Kotlin | `accessToken`, `tokenType` 필드를 가진 로그인 응답 DTO |
| `MemberService.login()` | Java | 인증 검증 후 `TokenProvider` 호출, `TokenResponse` 반환 |
| `MemberController.login()` | Kotlin | `ResponseEntity<BaseResponse<TokenResponse>>` 반환 |
| `JwtAuthenticationFilter` | Kotlin | 요청별 토큰 검증 및 `SecurityContext` 인증 정보 세팅 (`common/security`) |
| `SecurityConfig` | Java | `SecurityFilterChain` 및 인가 규칙 정의 (`common/config`) |

---

## Spring Security 필터 체인 & 인증된 요청 흐름

### SecurityConfig 인가 규칙

| 대상 | 규칙 |
|---|---|
| `POST /api/v1/members/signup` | `permitAll()` — 인증 없이 접근 가능 |
| `POST /api/v1/members/login` | `permitAll()` — 인증 없이 접근 가능 |
| 그 외 모든 요청 | `authenticated()` — 유효한 JWT 필요 |
| CSRF | 비활성화 (`csrf().disable()`) — REST API + JWT 조합에서는 불필요 |
| 세션 정책 | `SessionCreationPolicy.STATELESS` — 서버에 세션 저장 안 함 |
| 필터 순서 | `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 `addFilterBefore`로 등록 |

### 인증이 필요한 요청 처리 흐름 (`GET /api/v1/members/me` 예시)

```
클라이언트
    │  GET /api/v1/members/me
    │  Header: Authorization: Bearer <accessToken>
    ▼
JwtAuthenticationFilter.doFilterInternal()
    │  1) resolveToken()                → "Authorization" 헤더에서 "Bearer " 접두사 제거 후 토큰 추출
    │  2) tokenProvider.validateToken() → 서명/만료 검증
    │       │ valid                          │ invalid / 토큰 없음
    │       ▼                                 ▼
    │  3) email = getEmail(token)        그대로 다음 필터로 통과
    │     role  = getRole(token)         (SecurityContext 비어 있음)
    │  4) UsernamePasswordAuthenticationToken(
    │       principal = email,
    │       credentials = null,
    │       authorities = ["ROLE_<role>"]
    │     )
    │  5) SecurityContextHolder.getContext().authentication = ⬆
    ▼
SecurityConfig의 authorizeHttpRequests
    │  anyRequest().authenticated()
    │       │ 인증 정보 있음                  │ 인증 정보 없음
    │       ▼                                 ▼
    ▼                                    401/403 응답
MemberController.getMyInfo(authentication)
    │  authentication.name == email
    ▼
200 OK
{ success: true, message: "내 정보 조회에 성공했습니다.", data: "<email>" }
```

---

*다음 도메인 (나눔 게시글, 채팅 등) 완성 시 동일한 형식으로 섹션을 추가한다.*
