package com.marvrus.vocabularytest.model.dto.irt;

/**
 * 종료 조건 판정 결과를 담는 DTO
 */
public class TerminationResult {

    private boolean shouldTerminate;  // 종료 여부
    private String reason;            // 종료 사유

    public TerminationResult() {}

    public TerminationResult(boolean shouldTerminate, String reason) {
        this.shouldTerminate = shouldTerminate;
        this.reason = reason;
    }

    // 종료 사유 상수
    public static final String MIN_ITEMS_NOT_MET = "MIN_ITEMS_NOT_MET";
    public static final String MAX_ITEMS_REACHED = "MAX_ITEMS_REACHED";
    public static final String TARGET_SE_REACHED = "TARGET_SE_REACHED";
    public static final String EXTREME_RESPONSE_PATTERN = "EXTREME_RESPONSE_PATTERN";
    public static final String CONVERGENCE_DETECTED = "CONVERGENCE_DETECTED";
    public static final String NO_MORE_ITEMS = "NO_MORE_ITEMS";
    public static final String CONTINUE = "CONTINUE";

    // Getters and Setters
    public boolean isShouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
