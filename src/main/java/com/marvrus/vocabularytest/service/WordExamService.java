package com.marvrus.vocabularytest.service;

import com.marvrus.vocabularytest.model.WordExamAnswerForm;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordExam;

import java.util.Map;

public interface WordExamService {
    WordExam generateWordExam();

    Word getExamWord(Long wordExamSeqno, Integer examOrder);

    Map<String, Object> submitAnswer(Long wordExamSeqno, Integer examOrder, WordExamAnswerForm wordExamAnswerForm);

    WordExam examDone(Long wordExamSeqno);

    WordExam getWordExamProgress(Long wordExamSeqno);
}
