package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.marvrus.vocabularytest.model.enums.YesNo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "word_exam_detail")
public class WordExamDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_exam_detail_seqno")
    private Long wordExamDetailSeqno;

    @Column(name = "word_exam_seqno")
    private Long wordExamSeqno;

    @Column(name = "exam_order")
    private int examOrder;

    @Column(name = "word_seqno")
    private Long wordSeqno;

    @Column(name = "word_seqno_low_limit")
    private Long wordSeqnoLowLimit;

    @Column(name = "word_seqno_high_limit")
    private Long wordSeqnoHighLimit;

    @Column(name = "answer")
    private String answer;

    @Column(name = "correct_yn")
    private YesNo correctYn;

    @Column(name = "create_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDt;

    @OneToOne
    @JoinColumn(name = "word_seqno", insertable = false, updatable = false)
    private Word word;

    public Long getWordExamDetailSeqno() {
        return wordExamDetailSeqno;
    }

    public void setWordExamDetailSeqno(Long wordExamDetailSeqno) {
        this.wordExamDetailSeqno = wordExamDetailSeqno;
    }

    public Long getWordExamSeqno() {
        return wordExamSeqno;
    }

    public void setWordExamSeqno(Long wordExamSeqno) {
        this.wordExamSeqno = wordExamSeqno;
    }

    public int getExamOrder() {
        return examOrder;
    }

    public void setExamOrder(int examOrder) {
        this.examOrder = examOrder;
    }

    public Long getWordSeqno() {
        return wordSeqno;
    }

    public void setWordSeqno(Long wordSeqno) {
        this.wordSeqno = wordSeqno;
    }

    public Long getWordSeqnoLowLimit() {
        return wordSeqnoLowLimit;
    }

    public void setWordSeqnoLowLimit(Long wordSeqnoLowLimit) {
        this.wordSeqnoLowLimit = wordSeqnoLowLimit;
    }

    public Long getWordSeqnoHighLimit() {
        return wordSeqnoHighLimit;
    }

    public void setWordSeqnoHighLimit(Long wordSeqnoHighLimit) {
        this.wordSeqnoHighLimit = wordSeqnoHighLimit;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public YesNo getCorrectYn() {
        return correctYn;
    }

    public void setCorrectYn(YesNo correctYn) {
        this.correctYn = correctYn;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
