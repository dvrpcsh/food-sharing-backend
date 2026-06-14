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
│   │   └── CryptoConfig.kt              ← BCryptPasswordEncoder @Bean
│   ├── dto/
│   │   └── BaseResponse.kt              ← 공통 응답 포맷 (제네릭)
│   └── exception/
│       └── GlobalExceptionHandler.kt    ← 전역 예외 처리기
│
└── member/
    ├── controller/
    │   └── MemberController.kt          ← 회원 REST 컨트롤러 (Kotlin)
    ├── domain/
    │   ├── Member.kt                    ← JPA 엔티티 (Kotlin class)
    │   └── Role.kt                      ← 권한 Enum
    ├── dto/
    │   ├── SignUpRequest.kt             ← 회원가입 요청 DTO
    │   └── LoginRequest.kt              ← 로그인 요청 DTO
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
| `CryptoConfig` | Kotlin | `BCryptPasswordEncoder`를 Spring Bean으로 등록. Spring Security 전체 미사용 |
| `Member` | Kotlin | `members` 테이블 매핑 JPA 엔티티. `plugin.jpa`(no-arg) + `allOpen`(@Entity) 플러그인 적용 |
| `Role` | Kotlin | `YOUTH_CUSTOMER` / `PROVIDER` / `ADMIN` — `EnumType.STRING`으로 DB 저장 |
| `SignUpRequest` | Kotlin | Bean Validation 적용 (`@field:` 접두사 필수). 이메일·비밀번호·닉네임 필수, 전화번호 선택 |
| `LoginRequest` | Kotlin | 이메일 + 비밀번호 검증 |
| `MemberRepository` | Kotlin | `findByEmail()`, `existsByEmail()` 쿼리 메서드 제공 |
| `MemberService` | **Java** | 단일 생성자 주입(Lombok 없음). `signUp`: 중복 체크 → BCrypt 해시 → 저장. `login`: 조회 → 비밀번호 검증 |
| `MemberController` | Kotlin | Java `MemberService` 생성자 주입. Happy Path만 담당, 예외는 Handler에 위임 |

### Member 엔티티 필드

| 필드 | 타입 | 제약 | 설명 |
|---|---|---|---|
| `id` | `Long` | PK, AUTO_INCREMENT | 식별자 |
| `email` | `String` | UNIQUE, NOT NULL, 100자 | 로그인 키 |
| `password` | `String` | NOT NULL | BCrypt 해시값 저장 |
| `nickname` | `String` | NOT NULL, 30자 | 표시 이름 |
| `phoneNumber` | `String?` | NULL 허용, 20자 | 선택 입력 |
| `role` | `Role` | NOT NULL, STRING | 기본값 `YOUTH_CUSTOMER` |
| `createdAt` | `LocalDateTime` | NOT NULL, updatable=false | 가입일 |
| `updatedAt` | `LocalDateTime` | NOT NULL | 최종 수정일 |

### API 엔드포인트

| 메서드 | URL | 요청 Body | 성공 응답 | 실패 응답 |
|---|---|---|---|---|
| `POST` | `/api/v1/members/signup` | `SignUpRequest` | `200 { success: true, message: "회원가입이 완료되었습니다." }` | `400 { success: false, message: "이미 사용 중인 이메일..." }` |
| `POST` | `/api/v1/members/login` | `LoginRequest` | `200 { success: true, message: "로그인에 성공했습니다." }` | `400 { success: false, message: "비밀번호가 일치하지 않습니다." }` |

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

*다음 도메인 (나눔 게시글, 채팅 등) 완성 시 동일한 형식으로 섹션을 추가한다.*
