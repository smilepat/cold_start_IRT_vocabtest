package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
 * Cloze 학습 주제 Entity
 * 주제별로 문맥 기반 빈칸 채우기 학습을 제공
 */
@Entity
@Table(name = "cloze_theme")
public class ClozeTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long themeId;

    @Column(name = "theme_name", nullable = false, length = 100)
    private String themeName;  // 주제명 (예: "Business Meeting", "Travel", "Technology")

    @Column(name = "theme_name_ko", length = 100)
    private String themeNameKo;  // 한글 주제명

    @Column(name = "description", length = 500)
    private String description;  // 주제 설명

    @Column(name = "difficulty_level")
    private Integer difficultyLevel;  // 난이도 레벨 (1-5)

    @Column(name = "category", length = 50)
    private String category;  // 카테고리 (Business, Daily Life, Academic 등)

    @Column(name = "thumbnail_url", length = 255)
    private String thumbnailUrl;  // 썸네일 이미지 URL

    @Column(name = "display_order")
    private Integer displayOrder;  // 표시 순서

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

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ClozePassage> passages = new ArrayList<>();

    // Getters and Setters
    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeNameKo() {
        return themeNameKo;
    }

    public void setThemeNameKo(String themeNameKo) {
        this.themeNameKo = themeNameKo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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

    public List<ClozePassage> getPassages() {
        return passages;
    }

    public void setPassages(List<ClozePassage> passages) {
        this.passages = passages;
    }
}
