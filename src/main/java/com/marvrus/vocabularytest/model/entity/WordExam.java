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
}
