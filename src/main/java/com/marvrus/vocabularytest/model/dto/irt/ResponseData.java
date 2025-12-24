package com.marvrus.vocabularytest.model.dto.irt;

import com.marvrus.vocabularytest.model.entity.Word;

/**
 * 응답 데이터를 담는 DTO
 * IRT 계산에서 사용자의 응답 정보를 표현
 */
public class ResponseData {

    private Word word;        // 문항 정보 (IRT 모수 포함)
    private boolean correct;  // 정답 여부
    private Integer responseTimeMs;  // 응답 시간(밀리초, 선택적)

    public ResponseData() {}

    public ResponseData(Word word, boolean correct) {
        this.word = word;
        this.correct = correct;
    }

    public ResponseData(Word word, boolean correct, Integer responseTimeMs) {
        this.word = word;
        this.correct = correct;
        this.responseTimeMs = responseTimeMs;
    }

    // Getters and Setters
    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
}
