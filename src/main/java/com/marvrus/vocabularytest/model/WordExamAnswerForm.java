package com.marvrus.vocabularytest.model;

import io.swagger.annotations.ApiModelProperty;

public class WordExamAnswerForm {
    @ApiModelProperty("영어 단어")
    private String word;
    @ApiModelProperty("답안")
    private String answer;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
