# Cold Start IRT - Adaptive Prior CAT 시스템 구축 가이드

## 개요

사전 문항 모수 추정 없이 **빈도수 기반 초기 난이도**로 시작하고,
사용자 응답을 수집하여 점진적으로 문항 모수를 개선하는 IRT 기반 CAT 시스템.

---

## 1. 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Frontend (React)                            │
│  - 문항 표시 / 응답 수집 / 결과 시각화 / 신뢰구간 표시               │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Backend (Spring Boot)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│  │  CAT Engine  │  │ Calibration  │  │   Item Bank  │              │
│  │  (IRT 계산)   │  │   Service    │  │   Service    │              │
│  └──────────────┘  └──────────────┘  └──────────────┘              │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        Database (MySQL/H2)                          │
│  - 문항 정보 (모수 포함) / 응답 로그 / 시험 결과                      │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     Batch Processing (Spring Batch)                 │
│  - 문항 모수 재추정 (야간 배치)                                       │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. 3단계 접근법

```
┌─────────────────────────────────────────────────────────────────────┐
│                    Phase 1: Cold Start                               │
│  빈도수(frequency) → 초기 난이도(b) 추정                              │
│  변별도(a) = 고정값 (예: 1.0)                                         │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│                 Phase 2: Data Collection                             │
│  사용자 응답 데이터 축적 (정답/오답 + 능력 추정치)                     │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────────┐
│               Phase 3: Parameter Calibration                         │
│  축적된 데이터로 실제 문항 모수(a, b) 재추정                          │
└─────────────────────────────────────────────────────────────────────┘
                            ↓
                    (반복 개선)
```

---

## 3. 빈도수 → 난이도 변환 근거

언어학적으로 **단어 빈도와 난이도는 강한 음의 상관관계**가 있습니다:

| 빈도 순위 | 예시 단어 | 추정 난이도 |
|----------|----------|------------|
| 1-1000 | the, is, have | 매우 쉬움 (-2.5 ~ -1.5) |
| 1001-3000 | because, important | 쉬움 (-1.5 ~ -0.5) |
| 3001-5000 | achieve, significant | 보통 (-0.5 ~ +0.5) |
| 5001-7000 | eloquent, meticulous | 어려움 (+0.5 ~ +1.5) |
| 7001-9000 | obfuscate, perspicacious | 매우 어려움 (+1.5 ~ +2.5) |

**변환 공식:**
```
b = (rank / max_rank) × 5 - 2.5

예: rank=4500, max_rank=9000
b = (4500/9000) × 5 - 2.5 = 0.0 (중간 난이도)
```

---

## 4. 데이터베이스 설계

### 4.1 ERD

```
┌─────────────────────┐       ┌─────────────────────┐
│       word          │       │     word_exam       │
├─────────────────────┤       ├─────────────────────┤
│ word_seqno (PK)     │       │ word_exam_seqno (PK)│
│ word                │       │ initial_theta       │
│ korean (정답)        │       │ final_theta         │
│ detail_section      │       │ standard_error      │
│ difficulty (b)      │       │ exam_start_dt       │
│ discrimination (a)  │       │ exam_end_dt         │
│ guessing (c)        │       │ exam_done_yn        │
│ response_count      │       │ question_count      │
│ correct_count       │       └─────────┬───────────┘
│ last_calibrated     │                 │
└─────────┬───────────┘                 │
          │                             │
          │       ┌─────────────────────┴───────────┐
          │       │        word_exam_detail         │
          │       ├─────────────────────────────────┤
          │       │ detail_seqno (PK)               │
          └───────┤ word_exam_seqno (FK)            │
                  │ word_seqno (FK)                 │
                  │ exam_order                      │
                  │ answer                          │
                  │ correct_yn                      │
                  │ theta_before                    │
                  │ theta_after                     │
                  │ response_time_ms                │
                  └─────────────────────────────────┘
                              │
                              ▼
          ┌─────────────────────────────────────────┐
          │          word_response_log              │
          ├─────────────────────────────────────────┤
          │ log_id (PK)                             │
          │ word_seqno (FK)                         │
          │ theta_at_response                       │
          │ is_correct                              │
          │ response_time_ms                        │
          │ created_at                              │
          └─────────────────────────────────────────┘
```

### 4.2 SQL 스키마

```sql
-- 기존 word 테이블 확장
ALTER TABLE word ADD COLUMN difficulty DOUBLE DEFAULT NULL COMMENT 'IRT 난이도 모수 (b)';
ALTER TABLE word ADD COLUMN discrimination DOUBLE DEFAULT 1.0 COMMENT 'IRT 변별도 모수 (a)';
ALTER TABLE word ADD COLUMN guessing DOUBLE DEFAULT 0.25 COMMENT 'IRT 추측도 모수 (c)';
ALTER TABLE word ADD COLUMN response_count INT DEFAULT 0 COMMENT '총 응답 수';
ALTER TABLE word ADD COLUMN correct_count INT DEFAULT 0 COMMENT '정답 수';
ALTER TABLE word ADD COLUMN last_calibrated DATETIME COMMENT '마지막 캘리브레이션 시간';

-- 기존 word_exam 테이블 확장
ALTER TABLE word_exam ADD COLUMN initial_theta DOUBLE DEFAULT 0.0 COMMENT '초기 능력 추정치';
ALTER TABLE word_exam ADD COLUMN final_theta DOUBLE COMMENT '최종 능력 추정치';
ALTER TABLE word_exam ADD COLUMN standard_error DOUBLE COMMENT '측정 표준오차';
ALTER TABLE word_exam ADD COLUMN question_count INT DEFAULT 0 COMMENT '출제 문항 수';

-- 기존 word_exam_detail 테이블 확장
ALTER TABLE word_exam_detail ADD COLUMN theta_before DOUBLE COMMENT '응답 전 능력 추정치';
ALTER TABLE word_exam_detail ADD COLUMN theta_after DOUBLE COMMENT '응답 후 능력 추정치';
ALTER TABLE word_exam_detail ADD COLUMN response_time_ms INT COMMENT '응답 시간(ms)';

-- 캘리브레이션용 응답 로그 테이블
CREATE TABLE word_response_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_seqno BIGINT NOT NULL,
    theta_at_response DOUBLE NOT NULL COMMENT '응답 시점 능력 추정치',
    is_correct BOOLEAN NOT NULL,
    response_time_ms INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_word_seqno (word_seqno),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (word_seqno) REFERENCES word(word_seqno)
) COMMENT='문항 캘리브레이션용 응답 로그';

-- 캘리브레이션 이력 테이블
CREATE TABLE calibration_history (
    calibration_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_seqno BIGINT NOT NULL,
    old_difficulty DOUBLE,
    new_difficulty DOUBLE,
    old_discrimination DOUBLE,
    new_discrimination DOUBLE,
    sample_size INT COMMENT '캘리브레이션에 사용된 응답 수',
    calibrated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_seqno) REFERENCES word(word_seqno)
) COMMENT='문항 모수 캘리브레이션 이력';
```

---

## 5. 핵심 Java 클래스

### 5.1 Entity 클래스

```java
// Word.java - IRT 모수 추가
@Entity
@Table(name = "word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordSeqno;

    private String word;
    private String korean;  // 정답
    private Integer detailSection;  // 빈도 기반 순위 (1-9000)

    // IRT 모수
    @Column(name = "difficulty")
    private Double difficulty;  // b 모수

    @Column(name = "discrimination")
    private Double discrimination = 1.0;  // a 모수

    @Column(name = "guessing")
    private Double guessing = 0.25;  // c 모수 (4지선다)

    // 캘리브레이션 통계
    @Column(name = "response_count")
    private Integer responseCount = 0;

    @Column(name = "correct_count")
    private Integer correctCount = 0;

    @Column(name = "last_calibrated")
    private LocalDateTime lastCalibrated;

    // Getters, Setters...
}

// WordExam.java - Theta 추가
@Entity
@Table(name = "word_exam")
public class WordExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordExamSeqno;

    @Column(name = "initial_theta")
    private Double initialTheta = 0.0;

    @Column(name = "final_theta")
    private Double finalTheta;

    @Column(name = "standard_error")
    private Double standardError;

    @Column(name = "question_count")
    private Integer questionCount = 0;

    // 기존 필드들...
    private LocalDateTime examStartDt;
    private LocalDateTime examEndDt;

    @Enumerated(EnumType.STRING)
    private YesNo examDoneYn;

    @OneToMany(mappedBy = "wordExamSeqno", fetch = FetchType.LAZY)
    private List<WordExamDetail> wordExamDetails;

    // Getters, Setters...
}
```

### 5.2 IRT 계산 엔진

```java
@Component
public class IrtEngine {

    private static final double D = 1.702;  // 정규분포 근사 상수
    private static final int QUAD_POINTS = 61;  // 수치적분 구간점 수
    private static final double THETA_MIN = -3.0;
    private static final double THETA_MAX = 3.0;

    /**
     * 3-Parameter Logistic Model (3PL)
     * P(θ) = c + (1-c) / (1 + e^(-Da(θ-b)))
     */
    public double probability(double theta, Word word) {
        double a = word.getDiscrimination();
        double b = word.getDifficulty();
        double c = word.getGuessing();

        double exponent = -D * a * (theta - b);
        return c + (1 - c) / (1 + Math.exp(exponent));
    }

    /**
     * 문항 정보 함수 (Item Information Function)
     * I(θ) = a² × [(P(θ) - c)² / ((1-c)² × P(θ))] × (1 - P(θ))
     */
    public double itemInformation(double theta, Word word) {
        double a = word.getDiscrimination();
        double c = word.getGuessing();
        double p = probability(theta, word);

        if (p <= c || p >= 1.0) return 0.0;  // 경계 조건

        double numerator = a * a * Math.pow(p - c, 2) * (1 - p);
        double denominator = Math.pow(1 - c, 2) * p;

        return numerator / denominator;
    }

    /**
     * EAP (Expected A Posteriori) 능력 추정
     * 베이지안 방식 - 사전분포 N(0, 1) 사용
     */
    public ThetaEstimate estimateThetaEAP(List<ResponseData> responses) {
        // 구간점과 사전분포 가중치 설정
        double[] quadPoints = new double[QUAD_POINTS];
        double[] priorWeights = new double[QUAD_POINTS];
        double step = (THETA_MAX - THETA_MIN) / (QUAD_POINTS - 1);

        for (int i = 0; i < QUAD_POINTS; i++) {
            quadPoints[i] = THETA_MIN + i * step;
            // 표준정규분포 밀도
            priorWeights[i] = Math.exp(-quadPoints[i] * quadPoints[i] / 2)
                              / Math.sqrt(2 * Math.PI);
        }

        // 우도 함수 계산
        double[] likelihood = new double[QUAD_POINTS];
        Arrays.fill(likelihood, 1.0);

        for (ResponseData response : responses) {
            Word word = response.getWord();
            boolean correct = response.isCorrect();

            for (int i = 0; i < QUAD_POINTS; i++) {
                double p = probability(quadPoints[i], word);
                likelihood[i] *= correct ? p : (1 - p);
            }
        }

        // 사후분포 및 EAP 계산
        double numerator = 0.0;
        double denominator = 0.0;
        double varianceNumerator = 0.0;

        for (int i = 0; i < QUAD_POINTS; i++) {
            double posterior = likelihood[i] * priorWeights[i];
            numerator += quadPoints[i] * posterior;
            denominator += posterior;
        }

        double thetaEAP = numerator / denominator;

        // 사후분산 계산 (SE용)
        for (int i = 0; i < QUAD_POINTS; i++) {
            double posterior = likelihood[i] * priorWeights[i] / denominator;
            varianceNumerator += Math.pow(quadPoints[i] - thetaEAP, 2) * posterior;
        }

        double posteriorSD = Math.sqrt(varianceNumerator);

        return new ThetaEstimate(thetaEAP, posteriorSD);
    }

    /**
     * MLE (Maximum Likelihood Estimation) 능력 추정
     * Newton-Raphson 반복법 사용
     */
    public ThetaEstimate estimateThetaMLE(List<ResponseData> responses) {
        double theta = 0.0;  // 초기값
        double tolerance = 0.001;
        int maxIterations = 50;

        for (int iter = 0; iter < maxIterations; iter++) {
            double firstDerivative = 0.0;
            double secondDerivative = 0.0;

            for (ResponseData response : responses) {
                Word word = response.getWord();
                double a = word.getDiscrimination();
                double b = word.getDifficulty();
                double c = word.getGuessing();
                double p = probability(theta, word);
                double u = response.isCorrect() ? 1.0 : 0.0;

                double pStar = (p - c) / (1 - c);
                double w = pStar * (1 - pStar);

                firstDerivative += D * a * (u - p) * pStar / (p * (1 - c));
                secondDerivative -= D * D * a * a * w *
                    ((u - c) / (p * (1 - c)) - (u - p) * pStar / (p * p * (1 - c)));
            }

            if (Math.abs(secondDerivative) < 0.0001) break;

            double delta = firstDerivative / (-secondDerivative);
            theta += delta;

            // 범위 제한
            theta = Math.max(THETA_MIN, Math.min(THETA_MAX, theta));

            if (Math.abs(delta) < tolerance) break;
        }

        // 정보함수 기반 SE 계산
        double totalInfo = 0.0;
        for (ResponseData response : responses) {
            totalInfo += itemInformation(theta, response.getWord());
        }
        double se = 1.0 / Math.sqrt(Math.max(totalInfo, 0.01));

        return new ThetaEstimate(theta, se);
    }

    /**
     * 테스트 정보 함수 (Test Information Function)
     */
    public double testInformation(double theta, List<Word> items) {
        return items.stream()
            .mapToDouble(item -> itemInformation(theta, item))
            .sum();
    }
}

// 결과 객체
@Data
@AllArgsConstructor
public class ThetaEstimate {
    private double theta;
    private double standardError;

    public double getLowerBound95() {
        return theta - 1.96 * standardError;
    }

    public double getUpperBound95() {
        return theta + 1.96 * standardError;
    }
}

@Data
@AllArgsConstructor
public class ResponseData {
    private Word word;
    private boolean correct;
}
```

### 5.3 문항 선택 전략

```java
@Component
public class ItemSelector {

    @Autowired
    private IrtEngine irtEngine;

    /**
     * 최대 정보량 기준 문항 선택 (Maximum Information)
     */
    public Word selectByMaxInfo(double currentTheta,
                                List<Word> availableItems,
                                Set<Long> usedItemIds) {
        Word bestItem = null;
        double maxInfo = Double.NEGATIVE_INFINITY;

        for (Word item : availableItems) {
            if (usedItemIds.contains(item.getWordSeqno())) {
                continue;
            }

            double info = irtEngine.itemInformation(currentTheta, item);
            if (info > maxInfo) {
                maxInfo = info;
                bestItem = item;
            }
        }

        return bestItem;
    }

    /**
     * 내용 균형 + 최대 정보량 (Content Balancing)
     * 난이도 구간별 균형 있게 출제
     */
    public Word selectWithContentBalance(double currentTheta,
                                         List<Word> availableItems,
                                         Set<Long> usedItemIds,
                                         Map<String, Integer> contentCounts) {
        // 가장 적게 출제된 내용 영역 찾기
        String targetContent = contentCounts.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);

        // 해당 영역에서 최대 정보량 문항 선택
        List<Word> filteredItems = availableItems.stream()
            .filter(item -> !usedItemIds.contains(item.getWordSeqno()))
            .filter(item -> getContentArea(item).equals(targetContent))
            .collect(Collectors.toList());

        if (filteredItems.isEmpty()) {
            filteredItems = availableItems.stream()
                .filter(item -> !usedItemIds.contains(item.getWordSeqno()))
                .collect(Collectors.toList());
        }

        return selectByMaxInfo(currentTheta, filteredItems, usedItemIds);
    }

    /**
     * 노출 제어 (Exposure Control)
     * 과다 노출 문항 제한
     */
    public Word selectWithExposureControl(double currentTheta,
                                          List<Word> availableItems,
                                          Set<Long> usedItemIds,
                                          double maxExposureRate) {
        // 노출률 필터링
        List<Word> eligibleItems = availableItems.stream()
            .filter(item -> !usedItemIds.contains(item.getWordSeqno()))
            .filter(item -> getExposureRate(item) < maxExposureRate)
            .collect(Collectors.toList());

        if (eligibleItems.isEmpty()) {
            eligibleItems = availableItems.stream()
                .filter(item -> !usedItemIds.contains(item.getWordSeqno()))
                .collect(Collectors.toList());
        }

        // 확률적 선택 (Sympson-Hetter 방식)
        List<Word> candidates = new ArrayList<>();
        for (Word item : eligibleItems) {
            double info = irtEngine.itemInformation(currentTheta, item);
            if (info > 0.1) {  // 최소 정보량 기준
                candidates.add(item);
            }
        }

        if (candidates.isEmpty()) {
            return eligibleItems.get(new Random().nextInt(eligibleItems.size()));
        }

        // 상위 5개 중 랜덤 선택 (과다노출 방지)
        candidates.sort((a, b) -> Double.compare(
            irtEngine.itemInformation(currentTheta, b),
            irtEngine.itemInformation(currentTheta, a)
        ));

        int topN = Math.min(5, candidates.size());
        return candidates.get(new Random().nextInt(topN));
    }

    private String getContentArea(Word item) {
        int section = item.getDetailSection();
        if (section <= 1000) return "초등";
        if (section <= 3000) return "중등";
        if (section <= 6000) return "고등";
        return "고급";
    }

    private double getExposureRate(Word item) {
        // 실제 구현에서는 DB에서 조회
        return (double) item.getResponseCount() / 10000.0;
    }
}
```

### 5.4 종료 조건 판정

```java
@Component
public class TerminationRules {

    @Value("${cat.min-items:5}")
    private int minItems;

    @Value("${cat.max-items:30}")
    private int maxItems;

    @Value("${cat.target-se:0.3}")
    private double targetSE;

    /**
     * 종료 여부 판정
     */
    public TerminationResult checkTermination(ThetaEstimate estimate,
                                               int itemCount,
                                               List<ResponseData> responses) {
        // 1. 최소 문항 수 미달
        if (itemCount < minItems) {
            return new TerminationResult(false, "MIN_ITEMS_NOT_MET");
        }

        // 2. 최대 문항 수 도달
        if (itemCount >= maxItems) {
            return new TerminationResult(true, "MAX_ITEMS_REACHED");
        }

        // 3. 목표 SE 도달
        if (estimate.getStandardError() <= targetSE) {
            return new TerminationResult(true, "TARGET_SE_REACHED");
        }

        // 4. 극단 응답 패턴 (모두 정답/오답)
        long correctCount = responses.stream().filter(ResponseData::isCorrect).count();
        if (itemCount >= 10 && (correctCount == 0 || correctCount == itemCount)) {
            return new TerminationResult(true, "EXTREME_RESPONSE_PATTERN");
        }

        // 5. SE 변화 정체 (수렴 판정)
        if (itemCount >= 10 && isConverged(responses)) {
            return new TerminationResult(true, "CONVERGENCE_DETECTED");
        }

        return new TerminationResult(false, "CONTINUE");
    }

    private boolean isConverged(List<ResponseData> responses) {
        // 최근 5문제의 SE 변화량이 0.01 미만이면 수렴으로 판정
        // 실제 구현에서는 이전 추정치들을 저장해서 비교
        return false;
    }
}

@Data
@AllArgsConstructor
public class TerminationResult {
    private boolean shouldTerminate;
    private String reason;
}
```

### 5.5 CAT 서비스 (통합)

```java
@Service
@Transactional
public class IrtCatService {

    @Autowired private IrtEngine irtEngine;
    @Autowired private ItemSelector itemSelector;
    @Autowired private TerminationRules terminationRules;
    @Autowired private WordRepository wordRepository;
    @Autowired private WordExamRepository wordExamRepository;
    @Autowired private WordExamDetailRepository wordExamDetailRepository;
    @Autowired private ResponseLogRepository responseLogRepository;

    /**
     * 시험 시작
     */
    public WordExam startExam() {
        WordExam exam = new WordExam();
        exam.setInitialTheta(0.0);
        exam.setExamStartDt(LocalDateTime.now());
        exam.setExamDoneYn(YesNo.N);
        exam.setQuestionCount(0);
        exam = wordExamRepository.save(exam);

        // 첫 문항 선택 (θ=0 기준)
        Word firstItem = selectNextItem(exam, 0.0, new HashSet<>());
        createExamDetail(exam, firstItem, 1, 0.0);

        return exam;
    }

    /**
     * 답안 제출 및 다음 문항 생성
     */
    public SubmitResult submitAnswer(Long examId, String answer) {
        WordExam exam = wordExamRepository.findById(examId)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "시험을 찾을 수 없습니다."));

        List<WordExamDetail> details = exam.getWordExamDetails();
        WordExamDetail currentDetail = details.get(details.size() - 1);
        Word currentWord = currentDetail.getWord();

        // 정답 판정
        boolean isCorrect = currentWord.getKorean().equals(answer.trim());
        currentDetail.setAnswer(answer);
        currentDetail.setCorrectYn(isCorrect ? YesNo.Y : YesNo.N);
        currentDetail.setThetaBefore(getCurrentTheta(details));

        // 응답 데이터 구성
        List<ResponseData> responses = details.stream()
            .filter(d -> d.getCorrectYn() != null)
            .map(d -> new ResponseData(d.getWord(), d.getCorrectYn() == YesNo.Y))
            .collect(Collectors.toList());

        // Theta 재추정
        ThetaEstimate estimate = irtEngine.estimateThetaEAP(responses);
        currentDetail.setThetaAfter(estimate.getTheta());
        wordExamDetailRepository.save(currentDetail);

        // 응답 로그 저장 (캘리브레이션용)
        saveResponseLog(currentWord, estimate.getTheta(), isCorrect);

        // 종료 조건 확인
        int itemCount = details.size();
        TerminationResult termResult = terminationRules.checkTermination(
            estimate, itemCount, responses);

        if (termResult.isShouldTerminate()) {
            return finishExam(exam, estimate, termResult.getReason());
        }

        // 다음 문항 선택
        Set<Long> usedIds = details.stream()
            .map(WordExamDetail::getWordSeqno)
            .collect(Collectors.toSet());

        Word nextItem = selectNextItem(exam, estimate.getTheta(), usedIds);
        if (nextItem == null) {
            return finishExam(exam, estimate, "NO_MORE_ITEMS");
        }

        createExamDetail(exam, nextItem, itemCount + 1, estimate.getTheta());

        return SubmitResult.builder()
            .isExamEnd(false)
            .currentTheta(estimate.getTheta())
            .standardError(estimate.getStandardError())
            .questionCount(itemCount)
            .build();
    }

    /**
     * 시험 종료 처리
     */
    private SubmitResult finishExam(WordExam exam, ThetaEstimate estimate, String reason) {
        exam.setFinalTheta(estimate.getTheta());
        exam.setStandardError(estimate.getStandardError());
        exam.setExamDoneYn(YesNo.Y);
        exam.setExamEndDt(LocalDateTime.now());
        exam.setQuestionCount(exam.getWordExamDetails().size());

        // Theta를 어휘 수준으로 변환
        int vocabLevel = convertThetaToVocabLevel(estimate.getTheta());
        exam.setExamLevel(vocabLevel);

        wordExamRepository.save(exam);

        return SubmitResult.builder()
            .isExamEnd(true)
            .finalTheta(estimate.getTheta())
            .standardError(estimate.getStandardError())
            .vocabLevel(vocabLevel)
            .lowerBound95(estimate.getLowerBound95())
            .upperBound95(estimate.getUpperBound95())
            .terminationReason(reason)
            .questionCount(exam.getQuestionCount())
            .build();
    }

    /**
     * Theta → 어휘 수준 변환
     * θ: -3 ~ +3 → 레벨: 1 ~ 9
     */
    private int convertThetaToVocabLevel(double theta) {
        // θ를 0-9000 스케일로 변환
        double vocabCount = (theta + 3.0) / 6.0 * 9000.0;
        vocabCount = Math.max(0, Math.min(9000, vocabCount));

        // 1000단어 단위로 레벨 구분
        return (int) Math.ceil(vocabCount / 1000.0);
    }

    private Word selectNextItem(WordExam exam, double theta, Set<Long> usedIds) {
        List<Word> availableItems = wordRepository.findAll();
        return itemSelector.selectWithExposureControl(theta, availableItems, usedIds, 0.3);
    }

    private void createExamDetail(WordExam exam, Word word, int order, double theta) {
        WordExamDetail detail = new WordExamDetail();
        detail.setWordExamSeqno(exam.getWordExamSeqno());
        detail.setWordSeqno(word.getWordSeqno());
        detail.setExamOrder(order);
        detail.setThetaBefore(theta);
        wordExamDetailRepository.save(detail);
    }

    private double getCurrentTheta(List<WordExamDetail> details) {
        return details.isEmpty() ? 0.0 :
            details.get(details.size() - 1).getThetaAfter() != null ?
            details.get(details.size() - 1).getThetaAfter() : 0.0;
    }

    private void saveResponseLog(Word word, double theta, boolean correct) {
        WordResponseLog log = new WordResponseLog();
        log.setWordSeqno(word.getWordSeqno());
        log.setThetaAtResponse(theta);
        log.setIsCorrect(correct);
        responseLogRepository.save(log);

        // 단어별 통계 업데이트
        word.setResponseCount(word.getResponseCount() + 1);
        if (correct) {
            word.setCorrectCount(word.getCorrectCount() + 1);
        }
        wordRepository.save(word);
    }
}

@Data
@Builder
public class SubmitResult {
    private boolean isExamEnd;
    private Double currentTheta;
    private Double finalTheta;
    private Double standardError;
    private Double lowerBound95;
    private Double upperBound95;
    private Integer vocabLevel;
    private Integer questionCount;
    private String terminationReason;
}
```

---

## 6. 캘리브레이션 서비스

```java
@Service
public class CalibrationService {

    @Autowired private WordRepository wordRepository;
    @Autowired private ResponseLogRepository responseLogRepository;

    private static final int MIN_RESPONSES_FOR_CALIBRATION = 30;

    /**
     * Phase 1: 빈도 기반 초기 난이도 설정
     */
    public void initializeDifficultyFromFrequency() {
        List<Word> words = wordRepository.findAll();
        double maxSection = 9000.0;

        for (Word word : words) {
            // detailSection(1-9000) → difficulty(-2.5 ~ +2.5)
            double b = (word.getDetailSection() / maxSection) * 5.0 - 2.5;
            word.setDifficulty(b);
            word.setDiscrimination(1.0);  // 초기 고정값
            word.setGuessing(0.25);       // 4지선다
        }

        wordRepository.saveAll(words);
        log.info("Initialized {} words with frequency-based difficulty", words.size());
    }

    /**
     * Phase 3: 축적된 데이터로 문항 모수 재추정
     * 매일 새벽에 배치로 실행
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void recalibrateItems() {
        List<Word> candidates = wordRepository
            .findByResponseCountGreaterThanEqual(MIN_RESPONSES_FOR_CALIBRATION);

        int calibratedCount = 0;

        for (Word word : candidates) {
            List<WordResponseLog> logs = responseLogRepository
                .findByWordSeqno(word.getWordSeqno());

            if (logs.size() < MIN_RESPONSES_FOR_CALIBRATION) continue;

            // 난이도 재추정
            double newDifficulty = estimateDifficulty(logs);

            // 변별도 재추정
            double newDiscrimination = estimateDiscrimination(logs);
            newDiscrimination = Math.max(0.3, Math.min(3.0, newDiscrimination));

            // 유효성 검증
            if (isValidCalibration(word, newDifficulty, newDiscrimination)) {
                word.setDifficulty(newDifficulty);
                word.setDiscrimination(newDiscrimination);
                word.setLastCalibrated(LocalDateTime.now());
                calibratedCount++;
            }
        }

        wordRepository.saveAll(candidates);
        log.info("Recalibrated {} items", calibratedCount);
    }

    /**
     * 난이도 추정: 정답률 50%가 되는 능력 수준 찾기
     */
    private double estimateDifficulty(List<WordResponseLog> logs) {
        // θ 구간별 정답률 계산
        Map<Double, int[]> thetaBins = new TreeMap<>();

        for (WordResponseLog log : logs) {
            double bin = Math.round(log.getThetaAtResponse() * 4) / 4.0;  // 0.25 단위
            thetaBins.computeIfAbsent(bin, k -> new int[2]);
            thetaBins.get(bin)[0]++;  // 총 응답
            if (log.getIsCorrect()) {
                thetaBins.get(bin)[1]++;  // 정답
            }
        }

        // 보간법으로 정답률 50% 지점 찾기
        double prevTheta = -3.0, prevRate = 0.0;

        for (Map.Entry<Double, int[]> entry : thetaBins.entrySet()) {
            double theta = entry.getKey();
            double rate = (double) entry.getValue()[1] / entry.getValue()[0];

            if (prevRate < 0.5 && rate >= 0.5) {
                // 선형 보간
                return prevTheta + (0.5 - prevRate) / (rate - prevRate) * (theta - prevTheta);
            }

            prevTheta = theta;
            prevRate = rate;
        }

        // 찾지 못한 경우 평균 θ 반환
        return logs.stream()
            .mapToDouble(WordResponseLog::getThetaAtResponse)
            .average()
            .orElse(0.0);
    }

    /**
     * 변별도 추정: Point-Biserial 상관계수 기반
     */
    private double estimateDiscrimination(List<WordResponseLog> logs) {
        int n = logs.size();

        // θ 통계량
        double sumTheta = 0, sumThetaSq = 0;
        double sumCorrectTheta = 0;
        int correctCount = 0;

        for (WordResponseLog log : logs) {
            double theta = log.getThetaAtResponse();
            sumTheta += theta;
            sumThetaSq += theta * theta;

            if (log.getIsCorrect()) {
                sumCorrectTheta += theta;
                correctCount++;
            }
        }

        if (correctCount == 0 || correctCount == n) {
            return 1.0;  // 극단적 경우 기본값
        }

        double meanTheta = sumTheta / n;
        double varTheta = sumThetaSq / n - meanTheta * meanTheta;
        double sdTheta = Math.sqrt(varTheta);

        double p = (double) correctCount / n;
        double meanCorrectTheta = sumCorrectTheta / correctCount;

        // Point-Biserial 상관계수
        double rpb = (meanCorrectTheta - meanTheta) / sdTheta * Math.sqrt(p * (1 - p));

        // 변별도로 변환 (Lord의 공식 근사)
        return rpb * 1.7 / Math.sqrt(1 - rpb * rpb);
    }

    /**
     * 캘리브레이션 결과 유효성 검증
     */
    private boolean isValidCalibration(Word word, double newB, double newA) {
        // 난이도 변화가 너무 크면 제외 (±1.5 이내)
        if (word.getDifficulty() != null) {
            if (Math.abs(newB - word.getDifficulty()) > 1.5) {
                return false;
            }
        }

        // 변별도가 비현실적이면 제외
        if (newA < 0.1 || newA > 4.0) {
            return false;
        }

        return true;
    }
}
```

---

## 7. 설정 파일

```properties
# application.properties

# CAT 설정
cat.min-items=5
cat.max-items=30
cat.target-se=0.3
cat.initial-theta=0.0

# 캘리브레이션 설정
calibration.min-responses=30
calibration.max-difficulty-change=1.5
calibration.exposure-rate-limit=0.3

# IRT 설정
irt.model=3PL
irt.estimation-method=EAP
irt.quad-points=61
irt.theta-min=-3.0
irt.theta-max=3.0
```

---

## 8. 프론트엔드 결과 표시 (예시)

```jsx
// ResultDisplay.jsx
const ResultDisplay = ({ result }) => {
  const {
    finalTheta,
    standardError,
    lowerBound95,
    upperBound95,
    vocabLevel,
    questionCount
  } = result;

  // θ를 어휘 수로 변환
  const vocabCount = Math.round((finalTheta + 3) / 6 * 9000);
  const lowerCount = Math.round((lowerBound95 + 3) / 6 * 9000);
  const upperCount = Math.round((upperBound95 + 3) / 6 * 9000);

  return (
    <div className="result-container">
      <h2>어휘력 측정 결과</h2>

      <div className="main-result">
        <span className="vocab-count">{vocabCount.toLocaleString()}</span>
        <span className="unit">단어</span>
      </div>

      <div className="confidence-interval">
        <p>95% 신뢰구간: {lowerCount.toLocaleString()} ~ {upperCount.toLocaleString()} 단어</p>
        <p>측정 정밀도: ±{Math.round(standardError * 1000)} 단어</p>
      </div>

      <div className="level-badge">
        레벨 {vocabLevel}: {LEVEL_TEXT_MAP[vocabLevel - 1]}
      </div>

      <div className="test-info">
        <p>총 {questionCount}문제로 측정 완료</p>
      </div>
    </div>
  );
};
```

---

## 9. 데이터 축적에 따른 정확도 향상

| 응답 수 | 난이도 추정 오차 | 변별도 추정 오차 | Theta 추정 SE |
|--------|-----------------|-----------------|---------------|
| 0 (초기) | ±0.8 (빈도 기반) | N/A (고정 1.0) | ±0.5 |
| 50 | ±0.5 | ±0.4 | ±0.4 |
| 100 | ±0.3 | ±0.3 | ±0.35 |
| 500 | ±0.15 | ±0.15 | ±0.3 |
| 1000+ | ±0.1 | ±0.1 | ±0.28 |

---

## 10. 전체 흐름도

```
┌──────────────────────────────────────────────────────────────────┐
│                        사용자 시험 시작                           │
└──────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌──────────────────────────────────────────────────────────────────┐
│ 초기 θ = 0.0 설정                                                 │
│ (또는 이전 시험 결과가 있으면 해당 값 사용)                         │
└──────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
         ┌──────────────────────────────────────────┐
         │        현재 θ에서 정보량 최대 문항 선택      │◄─────┐
         └──────────────────────────────────────────┘      │
                                 │                         │
                                 ▼                         │
         ┌──────────────────────────────────────────┐      │
         │              문항 제시 및 응답 수집          │      │
         └──────────────────────────────────────────┘      │
                                 │                         │
                                 ▼                         │
         ┌──────────────────────────────────────────┐      │
         │           θ 재추정 (EAP/MLE)              │      │
         │           SE 계산                         │      │
         │           응답 로그 저장 ★                 │      │
         └──────────────────────────────────────────┘      │
                                 │                         │
                                 ▼                         │
                    ┌───────────────────────┐             │
                    │  SE < 0.3 또는         │─── No ─────┘
                    │  문항 수 ≥ 최대?        │
                    └───────────────────────┘
                                 │ Yes
                                 ▼
         ┌──────────────────────────────────────────┐
         │         시험 종료, 결과 제시               │
         │         θ → 어휘 수준 변환                │
         └──────────────────────────────────────────┘
                                 │
                                 ▼
         ┌──────────────────────────────────────────┐
         │      [배치] 축적된 데이터로 문항 모수 재추정  │
         │      (매일/매주 스케줄러)                   │
         └──────────────────────────────────────────┘
```

---

## 11. 구현 로드맵

| 단계 | 작업 | 우선순위 |
|-----|------|---------|
| **1** | DB 스키마 확장 | 필수 |
| **2** | 빈도→난이도 초기화 | 필수 |
| **3** | IRT 엔진 (확률, 정보함수) | 필수 |
| **4** | Theta 추정 (EAP) | 필수 |
| **5** | 문항 선택기 | 필수 |
| **6** | 종료 조건 판정 | 필수 |
| **7** | CAT 서비스 통합 | 필수 |
| **8** | 응답 로그 수집 | 필수 |
| **9** | 캘리브레이션 배치 | 중요 |
| **10** | 프론트엔드 수정 | 필수 |
| **11** | 테스트 및 검증 | 필수 |

---

## 12. 결론

### 이 방식의 장점

1. **대규모 사전 테스트 없이 IRT CAT 시작 가능**
2. **서비스 운영하면서 자연스럽게 정확도 향상**
3. **빈도수 기반 초기값이 "합리적인 추측"으로 작동**
4. **실제 응답 데이터가 쌓일수록 과학적 근거 확보**

### 핵심 요약

| 질문 | 답변 |
|-----|-----|
| 빈도수로 초기 난이도 대체 가능? | ✅ 가능. 언어학적 근거 있음 |
| 응답 데이터로 모수 재추정? | ✅ 가능. 온라인 캘리브레이션 방식 |
| 초기 정확도는? | 기존 이분법과 유사, 점진적 향상 |
| 필요 데이터 양? | 문항당 30~50개 응답부터 효과적 |
