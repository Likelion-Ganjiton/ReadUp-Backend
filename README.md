# ReadUp Backend

ReadUp은 사용자가 관심 분야의 뉴스를 읽고 직접 요약하면서  
뉴스 이해력과 요약 능력을 기를 수 있도록 돕는 서비스입니다.

작성한 요약문과 뉴스 원문을 비교해 AI 피드백을 제공하며,  
다른 사용자의 요약을 살펴보고 의견을 나눌 수 있습니다.

## 주요 기능

### 맞춤형 뉴스

- 관심 카테고리 설정
- 관심 분야를 바탕으로 오늘의 뉴스 추천
- 카테고리별 뉴스 목록 및 상세 내용 조회

### 요약 및 AI 피드백

- 뉴스 기사에 대한 요약문 작성
- 원문과 요약문의 벡터 유사도 비교
- AI를 활용한 점수, 잘한 점, 개선할 점 제공
- 요약문 공개·비공개 설정

### 커뮤니티

- 다른 사용자가 공개한 요약문 조회
- 카테고리별 커뮤니티 피드 제공
- 요약문 좋아요 및 댓글 작성

### 학습 기록

- 작성한 요약문과 AI 피드백 다시 보기
- 전체 요약 횟수와 평균 점수 확인
- 연속 학습 일수 확인

### 사용자 관리

- 회원가입 및 JWT 기반 로그인
- 관심 카테고리와 알림 시간 설정
- 이메일을 통한 아이디 및 비밀번호 찾기

## 기술 스택

- **Backend:** Java 17, Spring Boot, Spring Security
- **Database:** Spring Data JPA, MySQL/MariaDB
- **Cache:** Redis
- **Authentication:** JWT
- **AI:** Upstage Solar LLM, Upstage Embedding
- **Vector Database:** Qdrant
- **Build:** Maven
