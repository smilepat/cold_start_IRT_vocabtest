package com.marvrus.vocabularytest.service.irt;

import com.marvrus.vocabularytest.config.exception.ApiException;
import com.marvrus.vocabularytest.model.dto.irt.ResponseData;
import com.marvrus.vocabularytest.model.dto.irt.SubmitResult;
import com.marvrus.vocabularytest.model.dto.irt.TerminationResult;
import com.marvrus.vocabularytest.model.dto.irt.ThetaEstimate;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordExam;
import com.marvrus.vocabularytest.model.entity.WordExamDetail;
import com.marvrus.vocabularytest.model.entity.WordResponseLog;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.WordExamDetailRepository;
import com.marvrus.vocabularytest.repository.WordExamRepository;
import com.marvrus.vocabularytest.repository.WordRepository;
import com.marvrus.vocabularytest.repository.WordResponseLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * IRT 기반 CAT (Computerized Adaptive Testing) 서비스
 * 시험 진행, 능력 추정, 문항 선택을 통합 관리
 */
@Service
@Transactional
public class IrtCatService {

    private static final Logger logger = LoggerFactory.getLogger(IrtCatService.class);

    @Autowired
    private IrtEngine irtEngine;

    @Autowired
    private ItemSelector itemSelector;

    @Autowired
    private TerminationRules terminationRules;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private WordExamRepository wordExamRepository;

    @Autowired
    private WordExamDetailRepository wordExamDetailRepository;

    @Autowired
    private WordResponseLogRepository responseLogRepository;

    /**
     * 시험 시작
     * 새 시험을 생성하고 첫 문항을 선택
     *
     * @return 생성된 시험 정보
     */
    public WordExam startExam() {
        // 새 시험 생성
        WordExam exam = new WordExam();
        exam.setInitialTheta(0.0);
        exam.setExamStartDt(LocalDateTime.now());
        exam.setExamDoneYn(YesNo.N);
        exam.setQuestionCount(0);
        exam = wordExamRepository.save(exam);

        // 전체 문항 조회
        List<Word> allWords = wordRepository.findByActiveYn(YesNo.Y);
        if (allWords.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "사용 가능한 문항이 없습니다.");
        }

        // 첫 문항 선택 (θ=0 기준, 중간 난이도 문항)
        Word firstItem = itemSelector.selectInitialItem(allWords, 0.0);
        if (firstItem == null) {
            firstItem = allWords.get(new Random().nextInt(allWords.size()));
        }

        // 첫 문항 저장
        createExamDetail(exam, firstItem, 1, 0.0, 1.0);
        exam.setQuestionCount(1);
        wordExamRepository.save(exam);

        logger.info("Started new IRT CAT exam: examId={}, firstWord={}",
                exam.getWordExamSeqno(), firstItem.getWord());

        return wordExamRepository.findById(exam.getWordExamSeqno())
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "시험 생성 실패"));
    }

    /**
     * 답안 제출 및 다음 문항 생성
     *
     * @param examId 시험 ID
     * @param answer 사용자 답안
     * @param responseTimeMs 응답 시간(밀리초, 선택적)
     * @return 제출 결과 (시험 진행 상태 또는 종료 결과)
     */
    public SubmitResult submitAnswer(Long examId, String answer, Integer responseTimeMs) {
        // 시험 조회
        WordExam exam = wordExamRepository.findById(examId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "시험을 찾을 수 없습니다."));

        if (exam.getExamDoneYn() == YesNo.Y) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 종료된 시험입니다.");
        }

        List<WordExamDetail> details = exam.getWordExamDetails();
        if (details == null || details.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "시험 데이터 오류");
        }

        // 현재 문항 조회 (가장 마지막 문항)
        WordExamDetail currentDetail = details.stream()
                .max(Comparator.comparingInt(WordExamDetail::getExamOrder))
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "현재 문항을 찾을 수 없습니다."));

        Word currentWord = currentDetail.getWord();
        if (currentWord == null) {
            currentWord = wordRepository.findById(currentDetail.getWordSeqno())
                    .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "문항 정보 오류"));
        }

        // 정답 판정
        boolean isCorrect = checkAnswer(currentWord, answer);
        currentDetail.setAnswer(answer);
        currentDetail.setCorrectYn(isCorrect ? YesNo.Y : YesNo.N);
        currentDetail.setResponseTimeMs(responseTimeMs);
        wordExamDetailRepository.save(currentDetail);

        // 응답 데이터 구성 (이미 답안 제출된 문항들)
        List<ResponseData> responses = buildResponseData(details);

        // Theta 재추정
        ThetaEstimate estimate = irtEngine.estimateThetaEAP(responses);
        currentDetail.setThetaAfter(estimate.getTheta());
        currentDetail.setSeAfter(estimate.getStandardError());
        wordExamDetailRepository.save(currentDetail);

        // 응답 로그 저장 (캘리브레이션용)
        saveResponseLog(currentWord, estimate.getTheta(), isCorrect, responseTimeMs);

        // 종료 조건 확인
        int itemCount = details.size();
        TerminationResult termResult = terminationRules.checkTermination(
                estimate, itemCount, responses);

        if (termResult.isShouldTerminate()) {
            return finishExam(exam, estimate, termResult.getReason());
        }

        // 다음 문항 선택
        Set<Long> usedIds = details.stream()
                .map(WordExamDetail::getWordSeqno)
                .collect(Collectors.toSet());

        List<Word> availableWords = wordRepository.findByActiveYn(YesNo.Y);
        Word nextItem = itemSelector.selectWithExposureControl(
                estimate.getTheta(), availableWords, usedIds);

        if (nextItem == null) {
            return finishExam(exam, estimate, TerminationResult.NO_MORE_ITEMS);
        }

        // 다음 문항 저장
        double itemInfo = irtEngine.itemInformation(estimate.getTheta(), nextItem);
        createExamDetail(exam, nextItem, itemCount + 1, estimate.getTheta(), estimate.getStandardError());
        exam.setQuestionCount(itemCount + 1);
        wordExamRepository.save(exam);

        logger.debug("Answer submitted: examId={}, correct={}, theta={}, SE={}, nextWord={}",
                examId, isCorrect,
                String.format("%.4f", estimate.getTheta()),
                String.format("%.4f", estimate.getStandardError()),
                nextItem.getWord());

        return SubmitResult.builder()
                .examEnd(false)
                .currentTheta(estimate.getTheta())
                .standardError(estimate.getStandardError())
                .questionCount(itemCount)
                .lastAnswerCorrect(isCorrect)
                .build();
    }

    /**
     * 시험 종료 처리
     */
    private SubmitResult finishExam(WordExam exam, ThetaEstimate estimate, String reason) {
        exam.setFinalTheta(estimate.getTheta());
        exam.setStandardError(estimate.getStandardError());
        exam.setExamDoneYn(YesNo.Y);
        exam.setExamEndDt(LocalDateTime.now());
        exam.setTerminationReason(reason);

        // Theta를 어휘 수준으로 변환
        int vocabCount = estimate.toVocabCount();
        int vocabLevel = estimate.toLevel();
        exam.setExamLevel(vocabLevel);
        exam.setExamDetailSection(vocabCount);

        wordExamRepository.save(exam);

        logger.info("Exam finished: examId={}, theta={}, SE={}, vocabCount={}, level={}, reason={}",
                exam.getWordExamSeqno(),
                String.format("%.4f", estimate.getTheta()),
                String.format("%.4f", estimate.getStandardError()),
                vocabCount, vocabLevel, reason);

        return SubmitResult.builder()
                .examEnd(true)
                .finalTheta(estimate.getTheta())
                .standardError(estimate.getStandardError())
                .lowerBound95(estimate.getLowerBound95())
                .upperBound95(estimate.getUpperBound95())
                .vocabCount(vocabCount)
                .vocabLevel(vocabLevel)
                .questionCount(exam.getQuestionCount())
                .terminationReason(reason)
                .build();
    }

    /**
     * 시험 결과 조회
     */
    public WordExam getExamResult(Long examId) {
        return wordExamRepository.findById(examId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "시험을 찾을 수 없습니다."));
    }

    /**
     * 현재 문항 조회
     */
    public WordExamDetail getCurrentQuestion(Long examId) {
        WordExam exam = wordExamRepository.findById(examId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "시험을 찾을 수 없습니다."));

        if (exam.getExamDoneYn() == YesNo.Y) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 종료된 시험입니다.");
        }

        List<WordExamDetail> details = exam.getWordExamDetails();
        if (details == null || details.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "문항 정보가 없습니다.");
        }

        WordExamDetail currentDetail = details.stream()
                .filter(d -> d.getCorrectYn() == null)  // 아직 답안 제출 안 된 문항
                .findFirst()
                .orElse(details.get(details.size() - 1));

        // Word 객체가 null이면 명시적으로 로드
        if (currentDetail.getWord() == null && currentDetail.getWordSeqno() != null) {
            Word word = wordRepository.findById(currentDetail.getWordSeqno()).orElse(null);
            currentDetail.setWord(word);
        }

        return currentDetail;
    }

    // === Private Helper Methods ===

    /**
     * 정답 판정
     */
    private boolean checkAnswer(Word word, String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return false;
        }
        String correctAnswer = word.getKorean();
        if (correctAnswer == null) {
            correctAnswer = word.getAnswer();
        }
        return correctAnswer != null && correctAnswer.trim().equals(answer.trim());
    }

    /**
     * 응답 데이터 구성
     */
    private List<ResponseData> buildResponseData(List<WordExamDetail> details) {
        List<ResponseData> responses = new ArrayList<>();
        for (WordExamDetail detail : details) {
            if (detail.getCorrectYn() != null) {
                Word word = detail.getWord();
                if (word == null) {
                    word = wordRepository.findById(detail.getWordSeqno()).orElse(null);
                }
                if (word != null) {
                    responses.add(new ResponseData(
                            word,
                            detail.getCorrectYn() == YesNo.Y,
                            detail.getResponseTimeMs()
                    ));
                }
            }
        }
        return responses;
    }

    /**
     * 시험 상세 생성
     */
    private void createExamDetail(WordExam exam, Word word, int order,
                                   double theta, double se) {
        WordExamDetail detail = new WordExamDetail();
        detail.setWordExamSeqno(exam.getWordExamSeqno());
        detail.setWordSeqno(word.getWordSeqno());
        detail.setExamOrder(order);
        detail.setThetaBefore(theta);
        detail.setSeBefore(se);
        detail.setItemInformation(irtEngine.itemInformation(theta, word));
        detail.setCreateDt(LocalDateTime.now());
        wordExamDetailRepository.save(detail);
    }

    /**
     * 응답 로그 저장 (캘리브레이션용)
     */
    private void saveResponseLog(Word word, double theta, boolean correct, Integer responseTimeMs) {
        try {
            WordResponseLog log = new WordResponseLog();
            log.setWordSeqno(word.getWordSeqno());
            log.setThetaAtResponse(theta);
            log.setIsCorrect(correct);
            log.setResponseTimeMs(responseTimeMs);
            responseLogRepository.save(log);

            // 단어별 통계 업데이트
            word.setResponseCount(word.getResponseCount() + 1);
            if (correct) {
                word.setCorrectCount(word.getCorrectCount() + 1);
            }
            wordRepository.save(word);
        } catch (Exception e) {
            logger.warn("Failed to save response log: {}", e.getMessage());
        }
    }
}
