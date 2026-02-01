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
import java.util.ArrayList;
import java.util.List;

/**
 * Cloze 지문 Entity
 * 주제 내에서 문맥을 제공하는 지문
 */
@Entity
@Table(name = "cloze_passage")
public class ClozePassage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passage_id")
    private Long passageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    @JsonIgnore
    private ClozeTheme theme;

    @Column(name = "theme_id", insertable = false, updatable = false)
    private Long themeId;

    @Column(name = "title", length = 200)
    private String title;  // 지문 제목

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // 지문 내용 (빈칸은 {{1}}, {{2}} 형식으로 표시)

    @Column(name = "content_ko", columnDefinition = "TEXT")
    private String contentKo;  // 한글 해석

    @Column(name = "passage_order")
    private Integer passageOrder;  // 지문 순서

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

    @OneToMany(mappedBy = "passage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClozeBlank> blanks = new ArrayList<>();

    // Getters and Setters
    public Long getPassageId() {
        return passageId;
    }

    public void setPassageId(Long passageId) {
        this.passageId = passageId;
    }

    public ClozeTheme getTheme() {
        return theme;
    }

    public void setTheme(ClozeTheme theme) {
        this.theme = theme;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentKo() {
        return contentKo;
    }

    public void setContentKo(String contentKo) {
        this.contentKo = contentKo;
    }

    public Integer getPassageOrder() {
        return passageOrder;
    }

    public void setPassageOrder(Integer passageOrder) {
        this.passageOrder = passageOrder;
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

    public List<ClozeBlank> getBlanks() {
        return blanks;
    }

    public void setBlanks(List<ClozeBlank> blanks) {
        this.blanks = blanks;
    }
}
