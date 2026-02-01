package com.marvrus.vocabularytest.model.dto.learning;

public class LearningAnswerResult {
    private boolean correct;
    private String correctAnswer;
    private String word;
    private String koreanDef;
    private String englishDef;
    private String example;

    public LearningAnswerResult() {}

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getKoreanDef() { return koreanDef; }
    public void setKoreanDef(String koreanDef) { this.koreanDef = koreanDef; }

    public String getEnglishDef() { return englishDef; }
    public void setEnglishDef(String englishDef) { this.englishDef = englishDef; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
}
