package com.marvrus.vocabularytest.model.dto.irt;

/**
 * Theta 추정 결과를 담는 DTO
 * EAP 또는 MLE 방식으로 추정된 능력치와 표준오차 정보
 */
public class ThetaEstimate {

    private double theta;           // 추정된 능력치 (범위: -3.0 ~ +3.0)
    private double standardError;   // 표준오차 (SE)

    public ThetaEstimate() {}

    public ThetaEstimate(double theta, double standardError) {
        this.theta = theta;
        this.standardError = standardError;
    }

    /**
     * 95% 신뢰구간 하한
     */
    public double getLowerBound95() {
        return theta - 1.96 * standardError;
    }

    /**
     * 95% 신뢰구간 상한
     */
    public double getUpperBound95() {
        return theta + 1.96 * standardError;
    }

    /**
     * Theta를 어휘 수준(1-9000)으로 변환
     */
    public int toVocabCount() {
        double vocabCount = (theta + 3.0) / 6.0 * 9000.0;
        return (int) Math.max(0, Math.min(9000, Math.round(vocabCount)));
    }

    /**
     * Theta를 레벨(1-9)로 변환
     */
    public int toLevel() {
        int vocabCount = toVocabCount();
        return Math.max(1, Math.min(9, (int) Math.ceil(vocabCount / 1000.0)));
    }

    // Getters and Setters
    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getStandardError() {
        return standardError;
    }

    public void setStandardError(double standardError) {
        this.standardError = standardError;
    }

    @Override
    public String toString() {
        return String.format("ThetaEstimate{theta=%.4f, SE=%.4f, 95%%CI=[%.4f, %.4f], vocabCount=%d}",
                theta, standardError, getLowerBound95(), getUpperBound95(), toVocabCount());
    }
}
