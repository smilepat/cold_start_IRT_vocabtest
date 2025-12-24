package com.marvrus.vocabularytest.service.irt;

import com.marvrus.vocabularytest.model.dto.irt.ResponseData;
import com.marvrus.vocabularytest.model.dto.irt.ThetaEstimate;
import com.marvrus.vocabularytest.model.entity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * IRT (Item Response Theory) 계산 엔진
 * 3-Parameter Logistic Model (3PL)을 사용한 능력 추정
 */
@Component
public class IrtEngine {

    private static final Logger logger = LoggerFactory.getLogger(IrtEngine.class);

    // 정규분포 근사 상수 (로지스틱 → 정규분포 변환)
    private static final double D = 1.702;

    @Value("${irt.quad-points:61}")
    private int quadPoints;  // 수치적분 구간점 수

    @Value("${irt.theta-min:-3.0}")
    private double thetaMin;  // Theta 최솟값

    @Value("${irt.theta-max:3.0}")
    private double thetaMax;  // Theta 최댓값

    /**
     * 3-Parameter Logistic Model (3PL) 확률 계산
     * P(θ) = c + (1-c) / (1 + e^(-Da(θ-b)))
     *
     * @param theta 능력치
     * @param word 문항 (IRT 모수 포함)
     * @return 정답 확률
     */
    public double probability(double theta, Word word) {
        double a = getDiscrimination(word);
        double b = getDifficulty(word);
        double c = getGuessing(word);

        double exponent = -D * a * (theta - b);
        return c + (1 - c) / (1 + Math.exp(exponent));
    }

    /**
     * 문항 정보 함수 (Item Information Function)
     * I(θ) = a² × [(P(θ) - c)² / ((1-c)² × P(θ))] × (1 - P(θ))
     *
     * @param theta 능력치
     * @param word 문항
     * @return 정보량
     */
    public double itemInformation(double theta, Word word) {
        double a = getDiscrimination(word);
        double c = getGuessing(word);
        double p = probability(theta, word);

        // 경계 조건: p가 c와 같거나 1에 가까우면 정보량 0
        if (p <= c + 0.0001 || p >= 0.9999) {
            return 0.0;
        }

        double numerator = a * a * Math.pow(p - c, 2) * (1 - p);
        double denominator = Math.pow(1 - c, 2) * p;

        return numerator / denominator;
    }

    /**
     * EAP (Expected A Posteriori) 능력 추정
     * 베이지안 방식 - 사전분포 N(0, 1) 사용
     *
     * @param responses 응답 데이터 리스트
     * @return Theta 추정치와 표준오차
     */
    public ThetaEstimate estimateThetaEAP(List<ResponseData> responses) {
        if (responses == null || responses.isEmpty()) {
            return new ThetaEstimate(0.0, 1.0);
        }

        int numPoints = quadPoints > 0 ? quadPoints : 61;
        double[] quadPointsArr = new double[numPoints];
        double[] priorWeights = new double[numPoints];
        double step = (thetaMax - thetaMin) / (numPoints - 1);

        // 구간점과 사전분포 가중치 설정
        for (int i = 0; i < numPoints; i++) {
            quadPointsArr[i] = thetaMin + i * step;
            // 표준정규분포 밀도
            priorWeights[i] = Math.exp(-quadPointsArr[i] * quadPointsArr[i] / 2)
                    / Math.sqrt(2 * Math.PI);
        }

        // 우도 함수 계산
        double[] likelihood = new double[numPoints];
        Arrays.fill(likelihood, 1.0);

        for (ResponseData response : responses) {
            Word word = response.getWord();
            boolean correct = response.isCorrect();

            for (int i = 0; i < numPoints; i++) {
                double p = probability(quadPointsArr[i], word);
                // 수치 안정성을 위한 클리핑
                p = Math.max(0.0001, Math.min(0.9999, p));
                likelihood[i] *= correct ? p : (1 - p);
            }
        }

        // 사후분포 및 EAP 계산
        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < numPoints; i++) {
            double posterior = likelihood[i] * priorWeights[i];
            numerator += quadPointsArr[i] * posterior;
            denominator += posterior;
        }

        // denominator가 0에 가까우면 기본값 반환
        if (denominator < 1e-10) {
            return new ThetaEstimate(0.0, 1.0);
        }

        double thetaEAP = numerator / denominator;

        // 사후분산 계산 (SE용)
        double varianceNumerator = 0.0;
        for (int i = 0; i < numPoints; i++) {
            double posterior = likelihood[i] * priorWeights[i] / denominator;
            varianceNumerator += Math.pow(quadPointsArr[i] - thetaEAP, 2) * posterior;
        }

        double posteriorSD = Math.sqrt(varianceNumerator);

        logger.debug("EAP Estimation: theta={}, SE={}, responses={}",
                String.format("%.4f", thetaEAP),
                String.format("%.4f", posteriorSD),
                responses.size());

        return new ThetaEstimate(thetaEAP, posteriorSD);
    }

    /**
     * MLE (Maximum Likelihood Estimation) 능력 추정
     * Newton-Raphson 반복법 사용
     *
     * @param responses 응답 데이터 리스트
     * @return Theta 추정치와 표준오차
     */
    public ThetaEstimate estimateThetaMLE(List<ResponseData> responses) {
        if (responses == null || responses.isEmpty()) {
            return new ThetaEstimate(0.0, 1.0);
        }

        // 모든 응답이 동일한 경우 (모두 정답 또는 모두 오답) EAP 사용
        long correctCount = responses.stream().filter(ResponseData::isCorrect).count();
        if (correctCount == 0 || correctCount == responses.size()) {
            return estimateThetaEAP(responses);
        }

        double theta = 0.0;  // 초기값
        double tolerance = 0.001;
        int maxIterations = 50;

        for (int iter = 0; iter < maxIterations; iter++) {
            double firstDerivative = 0.0;
            double secondDerivative = 0.0;

            for (ResponseData response : responses) {
                Word word = response.getWord();
                double a = getDiscrimination(word);
                double b = getDifficulty(word);
                double c = getGuessing(word);
                double p = probability(theta, word);
                double u = response.isCorrect() ? 1.0 : 0.0;

                // 수치 안정성을 위한 클리핑
                p = Math.max(c + 0.0001, Math.min(0.9999, p));

                double pStar = (p - c) / (1 - c);
                double w = pStar * (1 - pStar);

                firstDerivative += D * a * (u - p) * pStar / (p * (1 - c));
                secondDerivative -= D * D * a * a * w *
                        ((u - c) / (p * (1 - c)) - (u - p) * pStar / (p * p * (1 - c)));
            }

            if (Math.abs(secondDerivative) < 0.0001) {
                break;
            }

            double delta = firstDerivative / (-secondDerivative);
            theta += delta;

            // 범위 제한
            theta = Math.max(thetaMin, Math.min(thetaMax, theta));

            if (Math.abs(delta) < tolerance) {
                break;
            }
        }

        // 정보함수 기반 SE 계산
        double totalInfo = 0.0;
        for (ResponseData response : responses) {
            totalInfo += itemInformation(theta, response.getWord());
        }
        double se = 1.0 / Math.sqrt(Math.max(totalInfo, 0.01));

        logger.debug("MLE Estimation: theta={}, SE={}, responses={}",
                String.format("%.4f", theta),
                String.format("%.4f", se),
                responses.size());

        return new ThetaEstimate(theta, se);
    }

    /**
     * 테스트 정보 함수 (Test Information Function)
     * 주어진 문항들의 총 정보량 계산
     *
     * @param theta 능력치
     * @param items 문항 리스트
     * @return 총 정보량
     */
    public double testInformation(double theta, List<Word> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> itemInformation(theta, item))
                .sum();
    }

    /**
     * 문항별 빈도 기반 초기 난이도 계산
     * detailSection(1-9000) → difficulty(-2.5 ~ +2.5)
     *
     * @param word 문항
     * @return 초기 난이도
     */
    public double calculateInitialDifficulty(Word word) {
        int section = word.getDetailSection();
        return (section / 9000.0) * 5.0 - 2.5;
    }

    // 헬퍼 메서드: null-safe 모수 반환
    private double getDiscrimination(Word word) {
        Double a = word.getDiscrimination();
        return (a != null && a > 0) ? a : 1.0;
    }

    private double getDifficulty(Word word) {
        Double b = word.getDifficulty();
        if (b != null) {
            return b;
        }
        // 난이도가 없으면 빈도 기반으로 계산
        return calculateInitialDifficulty(word);
    }

    private double getGuessing(Word word) {
        Double c = word.getGuessing();
        return (c != null && c >= 0 && c < 1) ? c : 0.25;
    }
}
