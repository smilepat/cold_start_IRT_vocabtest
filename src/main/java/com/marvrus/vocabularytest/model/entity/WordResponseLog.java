package com.marvrus.vocabularytest.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 문항 캘리브레이션용 응답 로그 엔티티
 * 사용자의 응답 데이터를 저장하여 후에 문항 모수 재추정에 사용
 */
@Entity
@Table(name = "word_response_log")
public class WordResponseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "word_seqno", nullable = false)
    private Long wordSeqno;

    @Column(name = "theta_at_response", nullable = false)
    private Double thetaAtResponse;  // 응답 시점 능력 추정치

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;  // 정답 여부

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;  // 응답 시간(밀리초)

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getWordSeqno() {
        return wordSeqno;
    }

    public void setWordSeqno(Long wordSeqno) {
        this.wordSeqno = wordSeqno;
    }

    public Double getThetaAtResponse() {
        return thetaAtResponse;
    }

    public void setThetaAtResponse(Double thetaAtResponse) {
        this.thetaAtResponse = thetaAtResponse;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Integer getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Integer responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
