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

    @Enumerated(EnumType.STRING)
    @Column(name = "correct_yn")
    private YesNo correctYn;

    @Column(name = "create_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDt;

    // IRT 관련 필드
    @Column(name = "theta_before")
    private Double thetaBefore;  // 응답 전 능력 추정치

    @Column(name = "theta_after")
    private Double thetaAfter;  // 응답 후 능력 추정치

    @Column(name = "se_before")
    private Double seBefore;  // 응답 전 표준오차

    @Column(name = "se_after")
    private Double seAfter;  // 응답 후 표준오차

    @Column(name = "item_information")
    private Double itemInformation;  // 해당 문항의 정보량

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;  // 응답 시간(밀리초)

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

    // IRT Getter/Setter
    public Double getThetaBefore() {
        return thetaBefore;
    }

    public void setThetaBefore(Double thetaBefore) {
        this.thetaBefore = thetaBefore;
    }

    public Double getThetaAfter() {
        return thetaAfter;
    }

    public void setThetaAfter(Double thetaAfter) {
        this.thetaAfter = thetaAfter;
    }

    public Double getSeBefore() {
        return seBefore;
    }

    public void setSeBefore(Double seBefore) {
        this.seBefore = seBefore;
    }

    public Double getSeAfter() {
        return seAfter;
    }

    public void setSeAfter(Double seAfter) {
        this.seAfter = seAfter;
    }

    public Double getItemInformation() {
        return itemInformation;
    }

    public void setItemInformation(Double itemInformation) {
        this.itemInformation = itemInformation;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }
}
