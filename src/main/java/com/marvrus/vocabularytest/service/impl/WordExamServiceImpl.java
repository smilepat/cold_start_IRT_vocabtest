package com.marvrus.vocabularytest.service.impl;

import com.marvrus.vocabularytest.config.exception.ApiException;
import com.marvrus.vocabularytest.model.WordExamAnswerForm;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordExam;
import com.marvrus.vocabularytest.model.entity.WordExamDetail;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.WordExamDetailRepository;
import com.marvrus.vocabularytest.repository.WordExamRepository;
import com.marvrus.vocabularytest.repository.WordRepository;
import com.marvrus.vocabularytest.service.WordExamService;
import com.marvrus.vocabularytest.utils.LocalDateTimeZoneUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class WordExamServiceImpl implements WordExamService {
    private static final int DETAIL_SECTION_BOTTOM = 1;
    private static final int DETAIL_SECTION_MIDDEL = 45;
    private static final int DETAIL_SECTION_TOP = 90;

    private final WordRepository wordRepository;
    private final WordExamRepository wordExamRepository;
    private final WordExamDetailRepository wordExamDetailRepository;

    @Autowired
    public WordExamServiceImpl(WordRepository wordRepository, WordExamRepository wordExamRepository,
                               WordExamDetailRepository wordExamDetailRepository) {
        this.wordRepository = wordRepository;
        this.wordExamRepository = wordExamRepository;
        this.wordExamDetailRepository = wordExamDetailRepository;
    }

    @Override
    @Transactional
    public WordExam generateWordExam() {
        WordExam wordExam = new WordExam();
        wordExam.setExamStartDt(LocalDateTimeZoneUtil.getNow());
        wordExam.setExamDoneYn(YesNo.N);
        wordExam = wordExamRepository.save(wordExam);

        WordExamDetail wordExamDetail = new WordExamDetail();
        wordExamDetail.setWordExamSeqno(wordExam.getWordExamSeqno());
        wordExamDetail.setExamOrder(1);
        List<Word> wordList = wordRepository.findAllByDetailSection(DETAIL_SECTION_MIDDEL);
        wordExamDetail.setWordSeqno(wordList.get(RandomUtils.nextInt(0, wordList.size())).getWordSeqno());
        wordExamDetailRepository.save(wordExamDetail);

        return wordExam;
    }

    @Override
    public Word getExamWord(Long wordExamSeqno, Integer examOrder) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        if (Objects.isNull(examOrder) || examOrder < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        if (wordExamResult.get().getExamDoneYn() == YesNo.Y) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 종료된 테스트입니다.");
        }

        WordExamDetail wordExamDetail = wordExamDetailRepository
                .findByWordExamSeqnoAndExamOrder(wordExamSeqno, examOrder);
        if (Objects.isNull(wordExamDetail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        return wordExamDetail.getWord();
    }

    @Override
    @Transactional
    public Map<String, Object> submitAnswer(Long wordExamSeqno, Integer examOrder,
                                            WordExamAnswerForm wordExamAnswerForm) {
        if (Objects.isNull(wordExamSeqno) || Objects.isNull(examOrder) || Objects
                .isNull(wordExamAnswerForm) || wordExamSeqno == 0 || examOrder < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        WordExam wordExam = wordExamResult.get();

        List<WordExamDetail> wordExamDetails = wordExam.getWordExamDetails();

        if (examOrder != wordExamDetails.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Word targetWord = wordRepository.findByWord(wordExamAnswerForm.getWord());
        if (Objects.isNull(targetWord)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "존재하지 않는 문제입니다.");
        }

        WordExamDetail wordExamDetail = wordExamDetailRepository
                .findByWordExamSeqnoAndExamOrder(wordExamSeqno, examOrder);
        if (Objects.isNull(wordExamDetail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        if (!StringUtils.equals(wordExamDetail.getWord().getWord(), wordExamAnswerForm.getWord())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        wordExamDetail.setCreateDt(LocalDateTimeZoneUtil.getNow());
        wordExamDetail.setAnswer(wordExamAnswerForm.getAnswer());

        String[] meanings = StringUtils.split(targetWord.getMeaning(), ",");

        YesNo correctYn = YesNo.N;
        for (String meaning : meanings) {
            if (StringUtils.equals(StringUtils.deleteWhitespace(meaning),
                    StringUtils.deleteWhitespace(wordExamAnswerForm.getAnswer()))) {
                correctYn = YesNo.Y;
                break;
            }
        }
        wordExamDetail.setCorrectYn(correctYn);
        wordExamDetailRepository.save(wordExamDetail);

        Map<String, Object> result = new HashMap<>();

        wordExamDetail.setWord(targetWord);
        wordExamDetails.add(wordExamDetail);

        boolean isExamEnd = getNextDetailSection(wordExamDetails) == targetWord.getDetailSection();

        if (!isExamEnd) {
            Word nextWord = getNextWord(wordExamDetails);
            if (Objects.isNull(nextWord)) {
                isExamEnd = true;
            } else {
                WordExamDetail nextWordExam = new WordExamDetail();
                nextWordExam.setWordExamSeqno(wordExam.getWordExamSeqno());
                nextWordExam.setExamOrder(examOrder + 1);
                nextWordExam.setWordSeqno(nextWord.getWordSeqno());
                wordExamDetailRepository.save(nextWordExam);
            }
        }

        result.put("isExamEnd", isExamEnd);
        return result;
    }

    private Word getNextWord(List<WordExamDetail> wordExamDetails) {
        List<Word> wordList = wordRepository.findAllByDetailSection(getNextDetailSection(wordExamDetails));
        Word nextWord = wordList.get(RandomUtils.nextInt(0, wordList.size()));

        for (WordExamDetail beforeExam : wordExamDetails) {
            if (nextWord.getWordSeqno().equals(beforeExam.getWordSeqno())) {
                if (wordList.size() == 1) {
                    return null;
                }
            }
        }

        return nextWord;
    }

    @Override
    public WordExam examDone(Long wordExamSeqno) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        WordExam wordExam = wordExamResult.get();

        List<WordExamDetail> wordExamDetailList = wordExam.getWordExamDetails();
        boolean isTestDone = !Objects.isNull(wordExamDetailList.get(wordExamDetailList.size() - 1).getCorrectYn());
        if (!isTestDone) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        int correctCnt = 0;
        for (WordExamDetail wordExamDetail : wordExamDetailList) {
            if (wordExamDetail.getCorrectYn() == YesNo.Y) {
                correctCnt++;
            }

            if (isTestDone) {
                int nextDetailSection = getNextDetailSection(wordExamDetailList);
                wordExam.setExamDetailSection(nextDetailSection);
                wordExam.setExamLevel((nextDetailSection - 1) / 10 + 1);
            }
        }

        wordExam.setScore(correctCnt);
        wordExam.setExamDoneYn(YesNo.Y);
        wordExam.setExamEndDt(LocalDateTimeZoneUtil.getNow());
        return wordExamRepository.save(wordExam);
    }

    private int getNextDetailSection(List<WordExamDetail> wordExamDetails) {
        double highest = DETAIL_SECTION_TOP;
        double lowest = DETAIL_SECTION_BOTTOM;

        for (WordExamDetail wordExamDetail : wordExamDetails) {
            if (wordExamDetail.getCorrectYn() == YesNo.Y) {
                lowest = wordExamDetail.getWord().getDetailSection();
            } else {
                highest = wordExamDetail.getWord().getDetailSection();
            }
        }

        return (int) Math.round((lowest + highest) / 2);
    }

    @Override
    public WordExam getWordExamProgress(Long wordExamSeqno) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        return wordExamResult.get();
    }
}
