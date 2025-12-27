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
@Table(name = "word")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_seqno")
    private Long wordSeqno;

    @Column(name = "level")
    private int level;

    @Column(name = "detail_section")
    private int detailSection;

    @Column(name = "word")
    private String word;

	@Column(name = "meaning")
    private String meaning;

    @Column(name = "example_sentence")
    private String exampleSentence;

    @Column(name = "korean")
    private String korean;

    @Column(name = "option1")
    private String option1;

    @Column(name = "option2")
    private String option2;

    @Column(name = "option3")
    private String option3;

    @Column(name = "unknown_option")
    private String unknown;

    @Column(name = "answer")
    private String answer;

    @Column(name = "active_yn")
    private YesNo activeYn;

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

    // IRT 모수 (Item Response Theory Parameters)
    @Column(name = "difficulty")
    private Double difficulty;  // b 모수 (난이도), 범위: -3.0 ~ +3.0

    @Column(name = "discrimination")
    private Double discrimination = 1.0;  // a 모수 (변별도), 범위: 0.3 ~ 3.0

    @Column(name = "guessing")
    private Double guessing = 0.25;  // c 모수 (추측도), 4지선다 기본값

    // 캘리브레이션 통계
    @Column(name = "response_count")
    private Integer responseCount = 0;  // 총 응답 수

    @Column(name = "correct_count")
    private Integer correctCount = 0;  // 정답 수

    @Column(name = "last_calibrated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastCalibrated;  // 마지막 캘리브레이션 시간

    public Long getWordSeqno() {
        return wordSeqno;
    }

    public void setWordSeqno(Long wordSeqno) {
        this.wordSeqno = wordSeqno;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDetailSection() {
        return detailSection;
    }

    public void setDetailSection(int detailSection) {
        this.detailSection = detailSection;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public String getKorean() {
 		return korean;
 	}

 	public void setKorean(String korean) {
 		this.korean = korean;
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

 	public String getUnknown() {
 		return unknown;
 	}

 	public void setUnknown(String unknown) {
 		this.unknown = unknown;
 	}

 	public String getAnswer() {
 		return answer;
 	}

 	public void setAnswer(String answer) {
 		this.answer = answer;
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

    // IRT 모수 Getter/Setter
    public Double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }

    public Double getDiscrimination() {
        return discrimination != null ? discrimination : 1.0;
    }

    public void setDiscrimination(Double discrimination) {
        this.discrimination = discrimination;
    }

    public Double getGuessing() {
        return guessing != null ? guessing : 0.25;
    }

    public void setGuessing(Double guessing) {
        this.guessing = guessing;
    }

    public Integer getResponseCount() {
        return responseCount != null ? responseCount : 0;
    }

    public void setResponseCount(Integer responseCount) {
        this.responseCount = responseCount;
    }

    public Integer getCorrectCount() {
        return correctCount != null ? correctCount : 0;
    }

    public void setCorrectCount(Integer correctCount) {
        this.correctCount = correctCount;
    }

    public LocalDateTime getLastCalibrated() {
        return lastCalibrated;
    }

    public void setLastCalibrated(LocalDateTime lastCalibrated) {
        this.lastCalibrated = lastCalibrated;
    }
}
