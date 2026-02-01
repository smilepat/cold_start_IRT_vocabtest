package com.marvrus.vocabularytest.controller.api;

import com.marvrus.vocabularytest.model.dto.ApiResponse;
import com.marvrus.vocabularytest.model.dto.learning.LearningAnswerResult;
import com.marvrus.vocabularytest.model.dto.learning.LearningQuestion;
import com.marvrus.vocabularytest.service.learning.LearningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning")
@Api(tags = "Learning API", description = "Vocabulary learning question endpoints")
@CrossOrigin(origins = "*")
public class LearningApiController {

    private static final Logger log = LoggerFactory.getLogger(LearningApiController.class);

    @Autowired
    private LearningService learningService;

    @GetMapping("/questions")
    @ApiOperation("Get learning questions by level")
    public ResponseEntity<ApiResponse<List<LearningQuestion>>> getQuestions(
            @ApiParam("Target level (1-9)") @RequestParam(defaultValue = "1") int level,
            @ApiParam("Question type filter (optional)") @RequestParam(required = false) String type,
            @ApiParam("Number of questions") @RequestParam(defaultValue = "10") int count) {

        log.info("Getting learning questions: level={}, type={}, count={}", level, type, count);

        if (level < 1 || level > 9) level = 1;
        if (count < 1) count = 1;
        if (count > 50) count = 50;

        List<LearningQuestion> questions = learningService.getQuestions(level, type, count);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @PostMapping("/check")
    @ApiOperation("Check answer for a learning question")
    public ResponseEntity<ApiResponse<LearningAnswerResult>> checkAnswer(
            @ApiParam("Word") @RequestParam String word,
            @ApiParam("Question type") @RequestParam String questionType,
            @ApiParam("User's answer (A, B, C, or D)") @RequestParam String userAnswer) {

        log.info("Checking answer: word={}, type={}, answer={}", word, questionType, userAnswer);

        LearningAnswerResult result = learningService.checkAnswer(word, questionType, userAnswer);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/types")
    @ApiOperation("Get available question types for a level")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getTypes(
            @ApiParam("Target level (1-9)") @RequestParam(defaultValue = "1") int level) {

        List<Map<String, String>> types = learningService.getAvailableTypes(level);
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/info")
    @ApiOperation("Get learning data info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("totalWords", learningService.getTotalWordCount());
        info.put("availableQuestionTypes", learningService.getAvailableTypes(1));
        return ResponseEntity.ok(ApiResponse.success(info));
    }
}
