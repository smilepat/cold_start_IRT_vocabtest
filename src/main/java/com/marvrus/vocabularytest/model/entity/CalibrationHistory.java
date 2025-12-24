package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 문항 모수 캘리브레이션 이력 엔티티
 * 문항 모수(난이도, 변별도)의 변경 이력을 추적
 */
@Entity
@Table(name = "calibration_history")
public class CalibrationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calibration_id")
    private Long calibrationId;

    @Column(name = "word_seqno", nullable = false)
    private Long wordSeqno;

    @Column(name = "old_difficulty")
    private Double oldDifficulty;

    @Column(name = "new_difficulty")
    private Double newDifficulty;

    @Column(name = "old_discrimination")
    private Double oldDiscrimination;

    @Column(name = "new_discrimination")
    private Double newDiscrimination;

    @Column(name = "sample_size")
    private Integer sampleSize;  // 캘리브레이션에 사용된 응답 수

    @Column(name = "calibrated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime calibratedAt;

    @PrePersist
    protected void onCreate() {
        calibratedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(Long calibrationId) {
        this.calibrationId = calibrationId;
    }

    public Long getWordSeqno() {
        return wordSeqno;
    }

    public void setWordSeqno(Long wordSeqno) {
        this.wordSeqno = wordSeqno;
    }

    public Double getOldDifficulty() {
        return oldDifficulty;
    }

    public void setOldDifficulty(Double oldDifficulty) {
        this.oldDifficulty = oldDifficulty;
    }

    public Double getNewDifficulty() {
        return newDifficulty;
    }

    public void setNewDifficulty(Double newDifficulty) {
        this.newDifficulty = newDifficulty;
    }

    public Double getOldDiscrimination() {
        return oldDiscrimination;
    }

    public void setOldDiscrimination(Double oldDiscrimination) {
        this.oldDiscrimination = oldDiscrimination;
    }

    public Double getNewDiscrimination() {
        return newDiscrimination;
    }

    public void setNewDiscrimination(Double newDiscrimination) {
        this.newDiscrimination = newDiscrimination;
    }

    public Integer getSampleSize() {
        return sampleSize;
    }

    public void setSampleSize(Integer sampleSize) {
        this.sampleSize = sampleSize;
    }

    public LocalDateTime getCalibratedAt() {
        return calibratedAt;
    }

    public void setCalibratedAt(LocalDateTime calibratedAt) {
        this.calibratedAt = calibratedAt;
    }
}
