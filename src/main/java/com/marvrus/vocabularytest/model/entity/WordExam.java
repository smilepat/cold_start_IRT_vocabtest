package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.marvrus.vocabularytest.model.enums.YesNo;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "word_exam")
public class WordExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_exam_seqno")
    @ApiModelProperty("어휘력테스트 일련번호")
    private Long wordExamSeqno;

    @Column(name = "exam_start_dt")
    @ApiModelProperty("어휘력테스트 시작일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime examStartDt;

    @Column(name = "exam_end_dt")
    @ApiModelProperty("어휘력테스트 종료일시")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime examEndDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_done_yn")
    @ApiModelProperty("어휘력테스트 완료여부")
    private YesNo examDoneYn;

    @Column(name = "score")
    @ApiModelProperty("점수")
    private int score;

    @Column(name = "exam_level")
    @ApiModelProperty("단어 레벨")
    private int examLevel;

    @Column(name = "exam_detail_section")
    @ApiModelProperty("단어 레벨 상세")
    private int examDetailSection;

    // IRT 관련 필드
    @Column(name = "initial_theta")
    @ApiModelProperty("초기 능력 추정치 (Theta)")
    private Double initialTheta = 0.0;

    @Column(name = "final_theta")
    @ApiModelProperty("최종 능력 추정치 (Theta)")
    private Double finalTheta;

    @Column(name = "standard_error")
    @ApiModelProperty("측정 표준오차 (SE)")
    private Double standardError;

    @Column(name = "question_count")
    @ApiModelProperty("출제 문항 수")
    private Integer questionCount = 0;

    @Column(name = "termination_reason")
    @ApiModelProperty("종료 사유")
    private String terminationReason;

    @OneToMany
    @JoinColumn(name = "word_exam_seqno", insertable = false, updatable = false)
    private List<WordExamDetail> wordExamDetails;

    public Long getWordExamSeqno() {
        return wordExamSeqno;
    }

    public void setWordExamSeqno(Long wordExamSeqno) {
        this.wordExamSeqno = wordExamSeqno;
    }

    public LocalDateTime getExamStartDt() {
        return examStartDt;
    }

    public void setExamStartDt(LocalDateTime examStartDt) {
        this.examStartDt = examStartDt;
    }

    public LocalDateTime getExamEndDt() {
        return examEndDt;
    }

    public void setExamEndDt(LocalDateTime examEndDt) {
        this.examEndDt = examEndDt;
    }

    public YesNo getExamDoneYn() {
        return examDoneYn;
    }

    public void setExamDoneYn(YesNo examDoneYn) {
        this.examDoneYn = examDoneYn;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getExamLevel() {
        return examLevel;
    }

    public void setExamLevel(int examLevel) {
        this.examLevel = examLevel;
    }

    public int getExamDetailSection() {
        return examDetailSection;
    }

    public void setExamDetailSection(int examDetailSection) {
        this.examDetailSection = examDetailSection;
    }

    public List<WordExamDetail> getWordExamDetails() {
        return wordExamDetails;
    }

    public void setWordExamDetails(List<WordExamDetail> wordExamDetails) {
        this.wordExamDetails = wordExamDetails;
    }

    // IRT Getter/Setter
    public Double getInitialTheta() {
        return initialTheta != null ? initialTheta : 0.0;
    }

    public void setInitialTheta(Double initialTheta) {
        this.initialTheta = initialTheta;
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

    public Integer getQuestionCount() {
        return questionCount != null ? questionCount : 0;
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
}
