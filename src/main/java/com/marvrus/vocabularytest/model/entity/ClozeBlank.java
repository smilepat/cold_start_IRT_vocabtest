package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.marvrus.vocabularytest.model.enums.YesNo;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Cloze 빈칸 Entity
 * 지문 내 개별 빈칸과 정답 정보
 */
@Entity
@Table(name = "cloze_blank")
public class ClozeBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blank_id")
    private Long blankId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passage_id", nullable = false)
    @JsonIgnore
    private ClozePassage passage;

    @Column(name = "passage_id", insertable = false, updatable = false)
    private Long passageId;

    @Column(name = "blank_number")
    private Integer blankNumber;  // 빈칸 번호 (지문 내에서 {{1}}, {{2}} 등)

    @Column(name = "answer", nullable = false, length = 100)
    private String answer;  // 정답 단어

    @Column(name = "answer_ko", length = 100)
    private String answerKo;  // 정답 한글 뜻

    @Column(name = "hint", length = 200)
    private String hint;  // 힌트

    @Column(name = "option1", length = 100)
    private String option1;  // 오답 선택지 1

    @Column(name = "option2", length = 100)
    private String option2;  // 오답 선택지 2

    @Column(name = "option3", length = 100)
    private String option3;  // 오답 선택지 3

    @Column(name = "word_class", length = 50)
    private String wordClass;  // 품사 (noun, verb, adjective 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "active_yn")
    private YesNo activeYn = YesNo.Y;

    @Column(name = "create_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDt;

    @Column(name = "update_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateDt;

    // Getters and Setters
    public Long getBlankId() {
        return blankId;
    }

    public void setBlankId(Long blankId) {
        this.blankId = blankId;
    }

    public ClozePassage getPassage() {
        return passage;
    }

    public void setPassage(ClozePassage passage) {
        this.passage = passage;
    }

    public Long getPassageId() {
        return passageId;
    }

    public void setPassageId(Long passageId) {
        this.passageId = passageId;
    }

    public Integer getBlankNumber() {
        return blankNumber;
    }

    public void setBlankNumber(Integer blankNumber) {
        this.blankNumber = blankNumber;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerKo() {
        return answerKo;
    }

    public void setAnswerKo(String answerKo) {
        this.answerKo = answerKo;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getWordClass() {
        return wordClass;
    }

    public void setWordClass(String wordClass) {
        this.wordClass = wordClass;
    }

    public YesNo getActiveYn() {
        return activeYn;
    }

    public void setActiveYn(YesNo activeYn) {
        this.activeYn = activeYn;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public LocalDateTime getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(LocalDateTime updateDt) {
        this.updateDt = updateDt;
    }
}
