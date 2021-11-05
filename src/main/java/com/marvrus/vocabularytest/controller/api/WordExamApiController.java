package com.marvrus.vocabularytest.controller.api;

import com.marvrus.vocabularytest.model.WordExamAnswerForm;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordExam;
import com.marvrus.vocabularytest.service.WordExamService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/word-exams")
public class WordExamApiController {
    private final WordExamService wordExamService;

    @Autowired
    public WordExamApiController(WordExamService wordExamService) {
        this.wordExamService = wordExamService;
    }

    @PostMapping("")
    @ApiOperation(value = "어휘력테스트 시작 요청", notes = "어휘력 테스트 시작요청")
    public WordExam generateWordExam() {
        return wordExamService.generateWordExam();
    }

    @GetMapping("/status")
    public String getStatus() {
    	return "Server is Running";
    }

    @GetMapping("/{wordExamSeqno}/orders/{examOrder}")
    @ApiOperation(value = "어휘력 테스트 문제 요청", notes = "어휘력 테스트 문제 요청")
    public Word getExamWord(
            @PathVariable("wordExamSeqno") @ApiParam("어휘력테스트 고유번호") Long wordExamSeqno,
            @PathVariable("examOrder") @ApiParam("문제 순서") Integer examOrder
    ) {
    	return wordExamService.getExamWord(wordExamSeqno, examOrder);
    }

    @PostMapping("/{wordExamSeqno}/orders/{examOrder}")
    @ApiOperation(value = "어휘력 테스트 문제별 답안 제출", notes = "어휘력 테스트 문제별 답안 제출")
    public Map<String, Object> submitAnswer(
            @PathVariable("wordExamSeqno") @ApiParam("어휘력테스트 고유번호") Long wordExamSeqno,
            @PathVariable("examOrder") @ApiParam("문제 순서") Integer examOrder,
            @RequestBody WordExamAnswerForm wordExamAnswerForm
    ) {
        return wordExamService.submitAnswer(wordExamSeqno, examOrder, wordExamAnswerForm);
    }

    @PostMapping("/{wordExamSeqno}")
    @ApiOperation(value = "어휘력 테스트 종료", notes = "어휘력 테스트 종료")
    public WordExam examDone(
            @PathVariable("wordExamSeqno") @ApiParam("어휘력테스트 고유번호") Long wordExamSeqno
    ) {
        return wordExamService.examDone(wordExamSeqno);
    }

    @GetMapping("/{wordExamSeqno}")
    @ApiOperation(value = "어휘력 테스트 진행상태 조회", notes = "어휘력 테스트 진행상태 조회")
    public WordExam getWordExamProgress(
            @PathVariable("wordExamSeqno") @ApiParam("어휘력테스트 고유번호") Long wordExamSeqno
    ) {
        return wordExamService.getWordExamProgress(wordExamSeqno);
    }
}
