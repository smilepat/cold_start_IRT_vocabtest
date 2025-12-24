package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.WordResponseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 응답 로그 Repository
 * 캘리브레이션을 위한 응답 데이터 조회
 */
@Repository
public interface WordResponseLogRepository extends JpaRepository<WordResponseLog, Long> {

    /**
     * 특정 문항의 모든 응답 로그 조회
     */
    List<WordResponseLog> findByWordSeqno(Long wordSeqno);

    /**
     * 특정 문항의 최근 N개 응답 로그 조회
     */
    List<WordResponseLog> findTop100ByWordSeqnoOrderByCreatedAtDesc(Long wordSeqno);

    /**
     * 특정 기간 내 응답 로그 조회
     */
    List<WordResponseLog> findByWordSeqnoAndCreatedAtBetween(
            Long wordSeqno, LocalDateTime start, LocalDateTime end);

    /**
     * 특정 문항의 응답 수 조회
     */
    long countByWordSeqno(Long wordSeqno);

    /**
     * 특정 문항의 정답 수 조회
     */
    @Query("SELECT COUNT(r) FROM WordResponseLog r WHERE r.wordSeqno = ?1 AND r.isCorrect = true")
    long countCorrectByWordSeqno(Long wordSeqno);

    /**
     * Theta 범위별 응답 로그 조회
     */
    List<WordResponseLog> findByWordSeqnoAndThetaAtResponseBetween(
            Long wordSeqno, Double minTheta, Double maxTheta);

    /**
     * 모든 응답 로그 수 조회
     */
    @Query("SELECT COUNT(r) FROM WordResponseLog r")
    long countAll();

    /**
     * 특정 날짜 이후 응답 로그 조회
     */
    List<WordResponseLog> findByCreatedAtAfter(LocalDateTime after);
}
