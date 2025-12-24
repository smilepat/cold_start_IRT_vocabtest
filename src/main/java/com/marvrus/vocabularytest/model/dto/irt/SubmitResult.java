package com.marvrus.vocabularytest.model.dto.irt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 답안 제출 결과를 담는 DTO
 * 시험 진행 중 또는 종료 시 결과 정보 제공
 */
@ApiModel(description = "IRT CAT 답안 제출 결과")
public class SubmitResult {

    @ApiModelProperty("시험 종료 여부")
    private boolean examEnd;

    @ApiModelProperty("현재 Theta 추정치")
    private Double currentTheta;

    @ApiModelProperty("최종 Theta 추정치 (시험 종료 시)")
    private Double finalTheta;

    @ApiModelProperty("표준오차 (SE)")
    private Double standardError;

    @ApiModelProperty("95% 신뢰구간 하한")
    private Double lowerBound95;

    @ApiModelProperty("95% 신뢰구간 상한")
    private Double upperBound95;

    @ApiModelProperty("어휘 수준 (1-9000)")
    private Integer vocabCount;

    @ApiModelProperty("어휘 레벨 (1-9)")
    private Integer vocabLevel;

    @ApiModelProperty("출제 문항 수")
    private Integer questionCount;

    @ApiModelProperty("종료 사유")
    private String terminationReason;

    @ApiModelProperty("정답 여부 (직전 문항)")
    private Boolean lastAnswerCorrect;

    // Builder 패턴
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubmitResult result = new SubmitResult();

        public Builder examEnd(boolean examEnd) {
            result.examEnd = examEnd;
            return this;
        }

        public Builder currentTheta(Double currentTheta) {
            result.currentTheta = currentTheta;
            return this;
        }

        public Builder finalTheta(Double finalTheta) {
            result.finalTheta = finalTheta;
            return this;
        }

        public Builder standardError(Double standardError) {
            result.standardError = standardError;
            return this;
        }

        public Builder lowerBound95(Double lowerBound95) {
            result.lowerBound95 = lowerBound95;
            return this;
        }

        public Builder upperBound95(Double upperBound95) {
            result.upperBound95 = upperBound95;
            return this;
        }

        public Builder vocabCount(Integer vocabCount) {
            result.vocabCount = vocabCount;
            return this;
        }

        public Builder vocabLevel(Integer vocabLevel) {
            result.vocabLevel = vocabLevel;
            return this;
        }

        public Builder questionCount(Integer questionCount) {
            result.questionCount = questionCount;
            return this;
        }

        public Builder terminationReason(String terminationReason) {
            result.terminationReason = terminationReason;
            return this;
        }

        public Builder lastAnswerCorrect(Boolean lastAnswerCorrect) {
            result.lastAnswerCorrect = lastAnswerCorrect;
            return this;
        }

        public SubmitResult build() {
            return result;
        }
    }

    // Getters and Setters
    public boolean isExamEnd() {
        return examEnd;
    }

    public void setExamEnd(boolean examEnd) {
        this.examEnd = examEnd;
    }

    public Double getCurrentTheta() {
        return currentTheta;
    }

    public void setCurrentTheta(Double currentTheta) {
        this.currentTheta = currentTheta;
    }

    public Double getFinalTheta() {
        return finalTheta;
    }

    public void setFinalTheta(Double finalTheta) {
        this.finalTheta = finalTheta;
    }

    public Double getStandardError() {
        return standardError;
    }

    public void setStandardError(Double standardError) {
        this.standardError = standardError;
    }

    public Double getLowerBound95() {
        return lowerBound95;
    }

    public void setLowerBound95(Double lowerBound95) {
        this.lowerBound95 = lowerBound95;
    }

    public Double getUpperBound95() {
        return upperBound95;
    }

    public void setUpperBound95(Double upperBound95) {
        this.upperBound95 = upperBound95;
    }

    public Integer getVocabCount() {
        return vocabCount;
    }

    public void setVocabCount(Integer vocabCount) {
        this.vocabCount = vocabCount;
    }

    public Integer getVocabLevel() {
        return vocabLevel;
    }

    public void setVocabLevel(Integer vocabLevel) {
        this.vocabLevel = vocabLevel;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public Boolean getLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    public void setLastAnswerCorrect(Boolean lastAnswerCorrect) {
        this.lastAnswerCorrect = lastAnswerCorrect;
    }
}
