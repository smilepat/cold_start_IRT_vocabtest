package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.CalibrationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 캘리브레이션 이력 Repository
 * 문항 모수 변경 이력 추적
 */
@Repository
public interface CalibrationHistoryRepository extends JpaRepository<CalibrationHistory, Long> {

    /**
     * 특정 문항의 캘리브레이션 이력 조회 (시간순)
     */
    List<CalibrationHistory> findByWordSeqnoOrderByCalibratedAtDesc(Long wordSeqno);

    /**
     * 특정 문항의 최근 캘리브레이션 조회
     */
    CalibrationHistory findTopByWordSeqnoOrderByCalibratedAtDesc(Long wordSeqno);

    /**
     * 특정 기간 내 캘리브레이션 이력 조회
     */
    List<CalibrationHistory> findByCalibratedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 특정 날짜 이후 캘리브레이션 수 조회
     */
    long countByCalibratedAtAfter(LocalDateTime after);

    /**
     * 전체 캘리브레이션 수 조회
     */
    @Query("SELECT COUNT(c) FROM CalibrationHistory c")
    long countAll();
}
