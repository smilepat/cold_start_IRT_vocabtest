package com.marvrus.vocabularytest.service.irt;

import com.marvrus.vocabularytest.model.entity.CalibrationHistory;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordResponseLog;
import com.marvrus.vocabularytest.repository.CalibrationHistoryRepository;
import com.marvrus.vocabularytest.repository.WordRepository;
import com.marvrus.vocabularytest.repository.WordResponseLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 문항 캘리브레이션 서비스
 * 축적된 응답 데이터로 문항 모수(난이도, 변별도)를 재추정
 */
@Service
public class CalibrationService {

    private static final Logger logger = LoggerFactory.getLogger(CalibrationService.class);

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordResponseLogRepository responseLogRepository;

    @Autowired
    private CalibrationHistoryRepository calibrationHistoryRepository;

    @Value("${calibration.min-responses:30}")
    private int minResponsesForCalibration;

    @Value("${calibration.max-difficulty-change:1.5}")
    private double maxDifficultyChange;

    private static final double MAX_SECTION = 9000.0;

    /**
     * Phase 1: 빈도 기반 초기 난이도 설정
     * 모든 문항에 대해 detailSection을 기반으로 초기 난이도 설정
     */
    @Transactional
    public int initializeDifficultyFromFrequency() {
        List<Word> words = wordRepository.findAll();
        int updatedCount = 0;

        for (Word word : words) {
            // 이미 난이도가 설정된 경우 건너뛰기 (선택적)
            // if (word.getDifficulty() != null) continue;

            // detailSection(1-9000) → difficulty(-2.5 ~ +2.5)
            double b = (word.getDetailSection() / MAX_SECTION) * 5.0 - 2.5;
            word.setDifficulty(b);
            word.setDiscrimination(1.0);  // 초기 고정값
            word.setGuessing(0.25);       // 4지선다
            updatedCount++;
        }

        wordRepository.saveAll(words);
        logger.info("Initialized {} words with frequency-based difficulty", updatedCount);

        return updatedCount;
    }

    /**
     * Phase 3: 축적된 데이터로 문항 모수 재추정
     * 매일 새벽 3시에 배치로 실행
     */
    @Scheduled(cron = "${calibration.schedule:0 0 3 * * *}")
    @Transactional
    public void recalibrateItems() {
        logger.info("Starting item recalibration...");

        List<Word> candidates = wordRepository.findByResponseCountGreaterThanEqual(minResponsesForCalibration);
        int calibratedCount = 0;
        int skippedCount = 0;

        for (Word word : candidates) {
            List<WordResponseLog> logs = responseLogRepository.findByWordSeqno(word.getWordSeqno());

            if (logs.size() < minResponsesForCalibration) {
                skippedCount++;
                continue;
            }

            try {
                // 난이도 재추정
                double newDifficulty = estimateDifficulty(logs);

                // 변별도 재추정
                double newDiscrimination = estimateDiscrimination(logs);
                newDiscrimination = Math.max(0.3, Math.min(3.0, newDiscrimination));

                // 유효성 검증
                if (isValidCalibration(word, newDifficulty, newDiscrimination)) {
                    // 캘리브레이션 이력 저장
                    saveCalibrationHistory(word, newDifficulty, newDiscrimination, logs.size());

                    // 모수 업데이트
                    word.setDifficulty(newDifficulty);
                    word.setDiscrimination(newDiscrimination);
                    word.setLastCalibrated(LocalDateTime.now());
                    wordRepository.save(word);

                    calibratedCount++;

                    logger.debug("Calibrated word {}: b={} -> {}, a={} -> {}",
                            word.getWordSeqno(),
                            String.format("%.4f", word.getDifficulty()),
                            String.format("%.4f", newDifficulty),
                            String.format("%.4f", word.getDiscrimination()),
                            String.format("%.4f", newDiscrimination));
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                logger.warn("Failed to calibrate word {}: {}", word.getWordSeqno(), e.getMessage());
                skippedCount++;
            }
        }

        logger.info("Recalibration completed: {} calibrated, {} skipped out of {} candidates",
                calibratedCount, skippedCount, candidates.size());
    }

    /**
     * 단일 문항 캘리브레이션 (수동 호출용)
     */
    @Transactional
    public boolean calibrateSingleItem(Long wordSeqno) {
        Word word = wordRepository.findById(wordSeqno).orElse(null);
        if (word == null) {
            return false;
        }

        List<WordResponseLog> logs = responseLogRepository.findByWordSeqno(wordSeqno);
        if (logs.size() < minResponsesForCalibration) {
            logger.info("Insufficient responses for calibration: {} (need {})",
                    logs.size(), minResponsesForCalibration);
            return false;
        }

        double newDifficulty = estimateDifficulty(logs);
        double newDiscrimination = estimateDiscrimination(logs);
        newDiscrimination = Math.max(0.3, Math.min(3.0, newDiscrimination));

        if (isValidCalibration(word, newDifficulty, newDiscrimination)) {
            saveCalibrationHistory(word, newDifficulty, newDiscrimination, logs.size());

            word.setDifficulty(newDifficulty);
            word.setDiscrimination(newDiscrimination);
            word.setLastCalibrated(LocalDateTime.now());
            wordRepository.save(word);

            return true;
        }

        return false;
    }

    /**
     * 난이도 추정: 정답률 50%가 되는 능력 수준 찾기
     */
    private double estimateDifficulty(List<WordResponseLog> logs) {
        // θ 구간별 정답률 계산
        Map<Double, int[]> thetaBins = new TreeMap<>();

        for (WordResponseLog log : logs) {
            // 0.25 단위로 구간화
            double bin = Math.round(log.getThetaAtResponse() * 4) / 4.0;
            thetaBins.computeIfAbsent(bin, k -> new int[2]);
            thetaBins.get(bin)[0]++;  // 총 응답
            if (Boolean.TRUE.equals(log.getIsCorrect())) {
                thetaBins.get(bin)[1]++;  // 정답
            }
        }

        // 보간법으로 정답률 50% 지점 찾기
        double prevTheta = -3.0;
        double prevRate = 0.0;

        for (Map.Entry<Double, int[]> entry : thetaBins.entrySet()) {
            double theta = entry.getKey();
            int[] counts = entry.getValue();
            double rate = counts[0] > 0 ? (double) counts[1] / counts[0] : 0.0;

            if (prevRate < 0.5 && rate >= 0.5) {
                // 선형 보간
                if (rate != prevRate) {
                    return prevTheta + (0.5 - prevRate) / (rate - prevRate) * (theta - prevTheta);
                }
            }

            prevTheta = theta;
            prevRate = rate;
        }

        // 찾지 못한 경우: 전체 응답의 평균 theta 반환
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
        if (n < 10) {
            return 1.0;  // 데이터 부족 시 기본값
        }

        // θ 통계량
        double sumTheta = 0;
        double sumThetaSq = 0;
        double sumCorrectTheta = 0;
        int correctCount = 0;

        for (WordResponseLog log : logs) {
            double theta = log.getThetaAtResponse();
            sumTheta += theta;
            sumThetaSq += theta * theta;

            if (Boolean.TRUE.equals(log.getIsCorrect())) {
                sumCorrectTheta += theta;
                correctCount++;
            }
        }

        // 극단적 경우 처리
        if (correctCount == 0 || correctCount == n) {
            return 1.0;
        }

        double meanTheta = sumTheta / n;
        double varTheta = sumThetaSq / n - meanTheta * meanTheta;

        if (varTheta <= 0) {
            return 1.0;
        }

        double sdTheta = Math.sqrt(varTheta);
        double p = (double) correctCount / n;
        double meanCorrectTheta = sumCorrectTheta / correctCount;

        // Point-Biserial 상관계수
        double rpb = (meanCorrectTheta - meanTheta) / sdTheta * Math.sqrt(p * (1 - p));

        // 상관계수가 너무 높거나 낮으면 제한
        rpb = Math.max(-0.9, Math.min(0.9, rpb));

        // 변별도로 변환 (Lord의 공식 근사)
        double discrimination = rpb * 1.7 / Math.sqrt(1 - rpb * rpb);

        return Math.max(0.3, Math.min(3.0, discrimination));
    }

    /**
     * 캘리브레이션 결과 유효성 검증
     */
    private boolean isValidCalibration(Word word, double newB, double newA) {
        // 난이도 변화가 너무 크면 제외
        if (word.getDifficulty() != null) {
            if (Math.abs(newB - word.getDifficulty()) > maxDifficultyChange) {
                logger.debug("Rejected calibration for word {}: difficulty change too large ({} -> {})",
                        word.getWordSeqno(), word.getDifficulty(), newB);
                return false;
            }
        }

        // 변별도가 비현실적이면 제외
        if (newA < 0.1 || newA > 4.0) {
            logger.debug("Rejected calibration for word {}: discrimination out of range ({})",
                    word.getWordSeqno(), newA);
            return false;
        }

        // 난이도가 범위를 벗어나면 제외
        if (newB < -4.0 || newB > 4.0) {
            logger.debug("Rejected calibration for word {}: difficulty out of range ({})",
                    word.getWordSeqno(), newB);
            return false;
        }

        return true;
    }

    /**
     * 캘리브레이션 이력 저장
     */
    private void saveCalibrationHistory(Word word, double newDifficulty,
                                         double newDiscrimination, int sampleSize) {
        CalibrationHistory history = new CalibrationHistory();
        history.setWordSeqno(word.getWordSeqno());
        history.setOldDifficulty(word.getDifficulty());
        history.setNewDifficulty(newDifficulty);
        history.setOldDiscrimination(word.getDiscrimination());
        history.setNewDiscrimination(newDiscrimination);
        history.setSampleSize(sampleSize);
        calibrationHistoryRepository.save(history);
    }

    /**
     * 캘리브레이션 통계 조회
     */
    public Map<String, Object> getCalibrationStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalWords = wordRepository.count();
        long calibratedWords = wordRepository.countByLastCalibratedIsNotNull();
        long wordsWithSufficientResponses = wordRepository.countByResponseCountGreaterThanEqual(minResponsesForCalibration);

        stats.put("totalWords", totalWords);
        stats.put("calibratedWords", calibratedWords);
        stats.put("wordsWithSufficientResponses", wordsWithSufficientResponses);
        stats.put("minResponsesForCalibration", minResponsesForCalibration);

        return stats;
    }
}
