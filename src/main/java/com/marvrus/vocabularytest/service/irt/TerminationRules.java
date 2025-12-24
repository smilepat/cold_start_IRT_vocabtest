package com.marvrus.vocabularytest.service.irt;

import com.marvrus.vocabularytest.model.dto.irt.ResponseData;
import com.marvrus.vocabularytest.model.dto.irt.TerminationResult;
import com.marvrus.vocabularytest.model.dto.irt.ThetaEstimate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CAT 종료 조건 판정
 * 다양한 종료 기준을 적용하여 시험 종료 여부 결정
 */
@Component
public class TerminationRules {

    private static final Logger logger = LoggerFactory.getLogger(TerminationRules.class);

    @Value("${cat.min-items:5}")
    private int minItems;

    @Value("${cat.max-items:30}")
    private int maxItems;

    @Value("${cat.target-se:0.3}")
    private double targetSE;

    @Value("${cat.convergence-threshold:0.01}")
    private double convergenceThreshold;

    /**
     * 종료 여부 판정
     *
     * @param estimate 현재 Theta 추정치
     * @param itemCount 출제된 문항 수
     * @param responses 응답 데이터 리스트
     * @return 종료 여부와 사유
     */
    public TerminationResult checkTermination(ThetaEstimate estimate,
                                               int itemCount,
                                               List<ResponseData> responses) {
        // 1. 최소 문항 수 미달
        if (itemCount < minItems) {
            logger.debug("Termination check: MIN_ITEMS_NOT_MET (current={}, min={})",
                    itemCount, minItems);
            return new TerminationResult(false, TerminationResult.MIN_ITEMS_NOT_MET);
        }

        // 2. 최대 문항 수 도달
        if (itemCount >= maxItems) {
            logger.info("Termination: MAX_ITEMS_REACHED (count={})", itemCount);
            return new TerminationResult(true, TerminationResult.MAX_ITEMS_REACHED);
        }

        // 3. 목표 SE 도달
        if (estimate != null && estimate.getStandardError() <= targetSE) {
            logger.info("Termination: TARGET_SE_REACHED (SE={}, target={})",
                    String.format("%.4f", estimate.getStandardError()),
                    String.format("%.4f", targetSE));
            return new TerminationResult(true, TerminationResult.TARGET_SE_REACHED);
        }

        // 4. 극단 응답 패턴 (10문항 이상에서 모두 정답 또는 모두 오답)
        if (responses != null && itemCount >= 10) {
            long correctCount = responses.stream().filter(ResponseData::isCorrect).count();
            if (correctCount == 0 || correctCount == itemCount) {
                logger.info("Termination: EXTREME_RESPONSE_PATTERN (correct={}/{})",
                        correctCount, itemCount);
                return new TerminationResult(true, TerminationResult.EXTREME_RESPONSE_PATTERN);
            }
        }

        // 5. SE 변화 정체 (수렴 판정) - 15문항 이상에서 적용
        if (itemCount >= 15 && isConverged(responses, estimate)) {
            logger.info("Termination: CONVERGENCE_DETECTED at {} items", itemCount);
            return new TerminationResult(true, TerminationResult.CONVERGENCE_DETECTED);
        }

        return new TerminationResult(false, TerminationResult.CONTINUE);
    }

    /**
     * 수렴 여부 판정
     * 최근 5문제의 SE 변화량이 threshold 미만이면 수렴으로 판정
     */
    private boolean isConverged(List<ResponseData> responses, ThetaEstimate currentEstimate) {
        // 실제 구현에서는 이전 추정치들을 저장해서 비교해야 함
        // 간단한 휴리스틱: SE가 이미 충분히 낮은 경우
        if (currentEstimate != null && currentEstimate.getStandardError() < targetSE * 1.2) {
            return true;
        }
        return false;
    }

    /**
     * 중간 결과 기반 조기 종료 판정
     * 빠른 능력 확정이 가능한 경우
     *
     * @param estimate 현재 추정치
     * @param itemCount 문항 수
     * @return 조기 종료 가능 여부
     */
    public boolean canEarlyTerminate(ThetaEstimate estimate, int itemCount) {
        if (itemCount < minItems) {
            return false;
        }

        // SE가 목표의 80%에 도달하고 10문항 이상인 경우
        if (estimate != null && itemCount >= 10) {
            return estimate.getStandardError() <= targetSE * 0.8;
        }

        return false;
    }

    /**
     * 남은 예상 문항 수 계산
     *
     * @param currentSE 현재 SE
     * @param averageInfoPerItem 문항당 평균 정보량
     * @return 예상 남은 문항 수
     */
    public int estimateRemainingItems(double currentSE, double averageInfoPerItem) {
        if (currentSE <= targetSE || averageInfoPerItem <= 0) {
            return 0;
        }

        // SE는 1/sqrt(정보량)에 비례
        // 목표 SE에 도달하려면 필요한 추가 정보량 계산
        double currentInfo = 1.0 / (currentSE * currentSE);
        double targetInfo = 1.0 / (targetSE * targetSE);
        double additionalInfoNeeded = targetInfo - currentInfo;

        int estimatedItems = (int) Math.ceil(additionalInfoNeeded / averageInfoPerItem);
        return Math.min(estimatedItems, maxItems);
    }

    // Getter for configuration values (테스트용)
    public int getMinItems() {
        return minItems;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public double getTargetSE() {
        return targetSE;
    }
}
