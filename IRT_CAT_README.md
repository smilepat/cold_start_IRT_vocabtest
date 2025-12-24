# IRT 기반 CAT 어휘력 평가 시스템

## 개요

이 시스템은 **Item Response Theory (IRT)** 기반의 **Computerized Adaptive Testing (CAT)** 을 구현한 어휘력 평가 시스템입니다.

### 특징

- **3-Parameter Logistic Model (3PL)** 사용
- **EAP (Expected A Posteriori)** 방식의 능력 추정
- **Cold Start 지원**: 빈도 기반 초기 난이도 설정
- **온라인 캘리브레이션**: 응답 데이터 축적 후 문항 모수 재추정
- **적응형 문항 선택**: 최대 정보량 기준 + 노출 제어

---

## 기존 시스템과의 차이점

| 구분 | 기존 시스템 (이분 탐색) | IRT CAT 시스템 |
|------|------------------------|----------------|
| 능력 추정 | 범위 기반 (1-9000) | Theta (-3 ~ +3) |
| 난이도 기준 | 순위 (detailSection) | IRT 난이도 모수 (b) |
| 문항 선택 | 중간값 + 무작위 | 정보량 최대화 |
| 종료 조건 | 범위 50 이하 | SE ≤ 0.3 또는 30문항 |
| 결과 정밀도 | 단일값 | 95% 신뢰구간 제공 |

---

## 프로젝트 구조

```
src/main/java/com/marvrus/vocabularytest/
├── controller/api/
│   └── IrtCatApiController.java      # IRT CAT API 엔드포인트
├── model/
│   ├── entity/
│   │   ├── Word.java                 # 문항 엔티티 (IRT 모수 추가)
│   │   ├── WordExam.java             # 시험 엔티티 (Theta 추가)
│   │   ├── WordExamDetail.java       # 시험 상세 (IRT 추적)
│   │   ├── WordResponseLog.java      # 응답 로그 (NEW)
│   │   └── CalibrationHistory.java   # 캘리브레이션 이력 (NEW)
│   └── dto/irt/
│       ├── ThetaEstimate.java        # Theta 추정 결과
│       ├── ResponseData.java         # 응답 데이터
│       ├── SubmitResult.java         # 제출 결과
│       └── TerminationResult.java    # 종료 조건 결과
├── repository/
│   ├── WordResponseLogRepository.java    # 응답 로그 조회 (NEW)
│   └── CalibrationHistoryRepository.java # 캘리브레이션 이력 조회 (NEW)
└── service/irt/
    ├── IrtEngine.java                # IRT 계산 엔진 (3PL, EAP, MLE)
    ├── ItemSelector.java             # 문항 선택 전략
    ├── TerminationRules.java         # 종료 조건 판정
    ├── IrtCatService.java            # CAT 서비스 (통합)
    └── CalibrationService.java       # 캘리브레이션 서비스
```

---

## API 엔드포인트

### IRT CAT 시험 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/irt/exam/start` | 새 IRT CAT 시험 시작 |
| POST | `/api/irt/exam/{examId}/submit` | 답안 제출 |
| GET | `/api/irt/exam/{examId}/current` | 현재 문항 조회 |
| GET | `/api/irt/exam/{examId}/result` | 시험 결과 조회 |

### 캘리브레이션 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/irt/calibration/initialize` | 빈도 기반 초기 난이도 설정 |
| POST | `/api/irt/calibration/run` | 캘리브레이션 수동 실행 |
| POST | `/api/irt/calibration/item/{wordSeqno}` | 단일 문항 캘리브레이션 |
| GET | `/api/irt/calibration/stats` | 캘리브레이션 통계 조회 |

---

## 설정 파일 (application.properties)

```properties
# CAT 종료 조건
cat.min-items=5           # 최소 문항 수
cat.max-items=30          # 최대 문항 수
cat.target-se=0.3         # 목표 표준오차

# IRT 모델 설정
irt.model=3PL
irt.estimation-method=EAP
irt.quad-points=61        # 수치적분 구간점 수
irt.theta-min=-3.0
irt.theta-max=3.0

# 캘리브레이션 설정
calibration.min-responses=30    # 캘리브레이션 최소 응답 수
calibration.max-difficulty-change=1.5
calibration.schedule=0 0 3 * * *  # 매일 새벽 3시
```

---

## 실행 방법

### 1. 데이터베이스 준비

```bash
# H2 사용 시 (기본 설정) - 자동 생성됨

# MySQL 사용 시
# 1) schema.sql 실행
# 2) schema-irt-extension.sql 실행 (IRT 확장)
# 3) sample_data.sql 실행
```

### 2. 애플리케이션 실행

```bash
# Windows
run.bat

# 또는 Maven 직접 실행
mvnw.cmd spring-boot:run -P local
```

### 3. 초기 난이도 설정 (최초 1회)

```bash
curl -X POST http://localhost:8080/api/irt/calibration/initialize
```

### 4. 시험 시작

```bash
# 시험 시작
curl -X POST http://localhost:8080/api/irt/exam/start

# 답안 제출
curl -X POST "http://localhost:8080/api/irt/exam/1/submit?answer=정답"

# 결과 조회
curl http://localhost:8080/api/irt/exam/1/result
```

---

## IRT 알고리즘 상세

### 3PL 모델

```
P(θ) = c + (1-c) / (1 + e^(-1.702 × a × (θ-b)))

θ: 능력 모수 (범위: -3 ~ +3)
a: 변별도 모수 (높을수록 능력 변별력 높음)
b: 난이도 모수 (높을수록 어려움)
c: 추측도 모수 (4지선다: 0.25)
```

### EAP 능력 추정

```
θ_EAP = ∫ θ × L(θ) × π(θ) dθ / ∫ L(θ) × π(θ) dθ

L(θ): 우도함수 (응답 패턴 기반)
π(θ): 사전분포 (표준정규분포 N(0,1))
```

### 정보 함수

```
I(θ) = a² × [(P(θ) - c)² / ((1-c)² × P(θ))] × (1 - P(θ))
```

---

## Cold Start 전략

1. **Phase 1 (초기)**: 빈도 기반 난이도
   - detailSection(1-9000) → difficulty(-2.5 ~ +2.5)
   - 변별도 = 1.0 (고정)
   - 추측도 = 0.25 (4지선다)

2. **Phase 2 (데이터 수집)**: 응답 로그 축적
   - 각 응답의 theta, 정답 여부 저장

3. **Phase 3 (캘리브레이션)**: 문항 모수 재추정
   - 30개 이상 응답 축적 시 재추정
   - Point-Biserial 상관계수 기반 변별도 추정

---

## 결과 해석

```json
{
  "examEnd": true,
  "finalTheta": 0.85,
  "standardError": 0.28,
  "lowerBound95": 0.30,
  "upperBound95": 1.40,
  "vocabCount": 6275,
  "vocabLevel": 7,
  "questionCount": 18,
  "terminationReason": "TARGET_SE_REACHED"
}
```

| 필드 | 설명 |
|------|------|
| finalTheta | 최종 능력 추정치 (-3 ~ +3) |
| standardError | 측정 표준오차 (낮을수록 정밀) |
| lowerBound95, upperBound95 | 95% 신뢰구간 |
| vocabCount | 추정 어휘 수 (1-9000) |
| vocabLevel | 어휘 레벨 (1-9) |
| terminationReason | 종료 사유 |

---

## 종료 조건

| 조건 | 설명 |
|------|------|
| TARGET_SE_REACHED | 목표 SE(0.3) 도달 - 정밀도 확보 |
| MAX_ITEMS_REACHED | 최대 문항 수(30) 도달 |
| EXTREME_RESPONSE_PATTERN | 10문항 이상에서 모두 정답/오답 |
| CONVERGENCE_DETECTED | SE 변화 정체 (수렴) |
| NO_MORE_ITEMS | 출제 가능 문항 소진 |

---

## 문의

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
