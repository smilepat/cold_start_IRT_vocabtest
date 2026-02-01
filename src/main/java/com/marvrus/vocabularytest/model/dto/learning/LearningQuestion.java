package com.marvrus.vocabularytest.model.dto.learning;

import java.util.List;

public class LearningQuestion {
    private String word;
    private String pos;
    private String koreanDef;
    private String englishDef;
    private String example;
    private String cefr;
    private String curriculum;
    private String questionType;
    private String questionTypeLabel;
    private String question;
    private List<String> choices;
    private List<String> choiceLabels;
    private String answer;

    public LearningQuestion() {}

    // Getters and Setters
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getPos() { return pos; }
    public void setPos(String pos) { this.pos = pos; }

    public String getKoreanDef() { return koreanDef; }
    public void setKoreanDef(String koreanDef) { this.koreanDef = koreanDef; }

    public String getEnglishDef() { return englishDef; }
    public void setEnglishDef(String englishDef) { this.englishDef = englishDef; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public String getCefr() { return cefr; }
    public void setCefr(String cefr) { this.cefr = cefr; }

    public String getCurriculum() { return curriculum; }
    public void setCurriculum(String curriculum) { this.curriculum = curriculum; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getQuestionTypeLabel() { return questionTypeLabel; }
    public void setQuestionTypeLabel(String questionTypeLabel) { this.questionTypeLabel = questionTypeLabel; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getChoices() { return choices; }
    public void setChoices(List<String> choices) { this.choices = choices; }

    public List<String> getChoiceLabels() { return choiceLabels; }
    public void setChoiceLabels(List<String> choiceLabels) { this.choiceLabels = choiceLabels; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
