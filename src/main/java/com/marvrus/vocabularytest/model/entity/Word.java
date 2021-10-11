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
