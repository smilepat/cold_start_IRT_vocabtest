package com.marvrus.vocabularytest.controller.api;

import com.marvrus.vocabularytest.model.dto.irt.SubmitResult;
import com.marvrus.vocabularytest.model.entity.WordExam;
import com.marvrus.vocabularytest.model.entity.WordExamDetail;
import com.marvrus.vocabularytest.service.irt.CalibrationService;
import com.marvrus.vocabularytest.service.irt.IrtCatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * IRT 기반 CAT API Controller
 * Theta 추정 기반 적응형 어휘력 평가 API
 */
@RestController
@RequestMapping("/api/irt")
@Api(tags = "IRT CAT API", description = "IRT 기반 Theta 추정 적응형 테스트 API")
public class IrtCatApiController {

    private static final Logger logger = LoggerFactory.getLogger(IrtCatApiController.class);

    @Autowired
    private IrtCatService irtCatService;

    @Autowired
    private CalibrationService calibrationService;

    /**
     * 시험 시작
     * 새 IRT CAT 시험을 시작하고 첫 문항을 반환
     */
    @PostMapping("/exam/start")
    @ApiOperation(value = "IRT CAT 시험 시작", notes = "새로운 IRT 기반 적응형 테스트를 시작합니다.")
    public ResponseEntity<WordExam> startExam() {
        logger.info("Starting new IRT CAT exam");
        WordExam exam = irtCatService.startExam();
        return ResponseEntity.ok(exam);
    }

    /**
     * 답안 제출
     * 현재 문항에 대한 답안을 제출하고 다음 문항 또는 결과 반환
     */
    @PostMapping("/exam/{examId}/submit")
    @ApiOperation(value = "답안 제출", notes = "현재 문항의 답안을 제출하고 다음 문항 또는 결과를 받습니다.")
    public ResponseEntity<SubmitResult> submitAnswer(
            @ApiParam(value = "시험 ID", required = true)
            @PathVariable Long examId,
            @ApiParam(value = "사용자 답안", required = true)
            @RequestParam String answer,
            @ApiParam(value = "응답 시간(밀리초)")
            @RequestParam(required = false) Integer responseTimeMs) {

        logger.debug("Submitting answer for exam {}: answer={}", examId, answer);
        SubmitResult result = irtCatService.submitAnswer(examId, answer, responseTimeMs);
        return ResponseEntity.ok(result);
    }

    /**
     * 현재 문항 조회
     * 진행 중인 시험의 현재 문항 정보 조회
     */
    @GetMapping("/exam/{examId}/current")
    @ApiOperation(value = "현재 문항 조회", notes = "현재 풀어야 할 문항 정보를 조회합니다.")
    public ResponseEntity<WordExamDetail> getCurrentQuestion(
            @ApiParam(value = "시험 ID", required = true)
            @PathVariable Long examId) {

        WordExamDetail detail = irtCatService.getCurrentQuestion(examId);
        return ResponseEntity.ok(detail);
    }

    /**
     * 시험 결과 조회
     * 완료된 시험의 결과 정보 조회
     */
    @GetMapping("/exam/{examId}/result")
    @ApiOperation(value = "시험 결과 조회", notes = "완료된 시험의 결과를 조회합니다.")
    public ResponseEntity<WordExam> getExamResult(
            @ApiParam(value = "시험 ID", required = true)
            @PathVariable Long examId) {

        WordExam exam = irtCatService.getExamResult(examId);
        return ResponseEntity.ok(exam);
    }

    /**
     * 초기 난이도 설정 (빈도 기반)
     * 모든 문항에 대해 빈도 기반 초기 난이도를 설정
     */
    @PostMapping("/calibration/initialize")
    @ApiOperation(value = "초기 난이도 설정", notes = "빈도 기반으로 모든 문항의 초기 난이도를 설정합니다.")
    public ResponseEntity<Map<String, Object>> initializeDifficulty() {
        logger.info("Initializing difficulty from frequency");
        int count = calibrationService.initializeDifficultyFromFrequency();
        return ResponseEntity.ok(Map.of(
                "message", "초기 난이도 설정 완료",
                "updatedCount", count
        ));
    }

    /**
     * 문항 캘리브레이션 실행
     * 축적된 응답 데이터로 문항 모수 재추정 (수동 트리거)
     */
    @PostMapping("/calibration/run")
    @ApiOperation(value = "캘리브레이션 실행", notes = "축적된 응답 데이터로 문항 모수를 재추정합니다.")
    public ResponseEntity<Map<String, Object>> runCalibration() {
        logger.info("Running manual calibration");
        calibrationService.recalibrateItems();
        return ResponseEntity.ok(Map.of(
                "message", "캘리브레이션 실행 완료",
                "stats", calibrationService.getCalibrationStats()
        ));
    }

    /**
     * 단일 문항 캘리브레이션
     * 특정 문항의 모수 재추정
     */
    @PostMapping("/calibration/item/{wordSeqno}")
    @ApiOperation(value = "단일 문항 캘리브레이션", notes = "특정 문항의 모수를 재추정합니다.")
    public ResponseEntity<Map<String, Object>> calibrateSingleItem(
            @ApiParam(value = "문항 ID", required = true)
            @PathVariable Long wordSeqno) {

        logger.info("Calibrating single item: {}", wordSeqno);
        boolean success = calibrationService.calibrateSingleItem(wordSeqno);
        return ResponseEntity.ok(Map.of(
                "message", success ? "캘리브레이션 성공" : "캘리브레이션 실패 (데이터 부족 또는 유효하지 않은 결과)",
                "wordSeqno", wordSeqno,
                "success", success
        ));
    }

    /**
     * 캘리브레이션 통계 조회
     */
    @GetMapping("/calibration/stats")
    @ApiOperation(value = "캘리브레이션 통계 조회", notes = "캘리브레이션 관련 통계 정보를 조회합니다.")
    public ResponseEntity<Map<String, Object>> getCalibrationStats() {
        Map<String, Object> stats = calibrationService.getCalibrationStats();
        return ResponseEntity.ok(stats);
    }
}
