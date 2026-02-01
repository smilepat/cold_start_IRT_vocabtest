package com.marvrus.vocabularytest.controller.api;

import com.marvrus.vocabularytest.model.dto.ApiResponse;
import com.marvrus.vocabularytest.model.entity.ClozeBlank;
import com.marvrus.vocabularytest.model.entity.ClozePassage;
import com.marvrus.vocabularytest.model.entity.ClozeTheme;
import com.marvrus.vocabularytest.service.cloze.ClozeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cloze")
@Api(tags = "Cloze Learning API")
public class ClozeApiController {

    private static final Logger logger = LoggerFactory.getLogger(ClozeApiController.class);

    @Autowired
    private ClozeService clozeService;

    // ===== Theme Endpoints =====

    @GetMapping("/themes")
    @ApiOperation("Get all active themes")
    public ResponseEntity<ApiResponse<List<ClozeTheme>>> getAllThemes() {
        logger.info("Getting all active cloze themes");
        List<ClozeTheme> themes = clozeService.getAllActiveThemes();
        return ResponseEntity.ok(ApiResponse.success(themes));
    }

    @GetMapping("/themes/category/{category}")
    @ApiOperation("Get themes by category")
    public ResponseEntity<ApiResponse<List<ClozeTheme>>> getThemesByCategory(@PathVariable String category) {
        logger.info("Getting themes by category: {}", category);
        List<ClozeTheme> themes = clozeService.getThemesByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(themes));
    }

    @GetMapping("/themes/difficulty/{level}")
    @ApiOperation("Get themes by difficulty level")
    public ResponseEntity<ApiResponse<List<ClozeTheme>>> getThemesByDifficulty(@PathVariable Integer level) {
        logger.info("Getting themes by difficulty level: {}", level);
        List<ClozeTheme> themes = clozeService.getThemesByDifficulty(level);
        return ResponseEntity.ok(ApiResponse.success(themes));
    }

    @GetMapping("/categories")
    @ApiOperation("Get all distinct categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        logger.info("Getting all categories");
        List<String> categories = clozeService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/themes/{themeId}")
    @ApiOperation("Get theme by ID")
    public ResponseEntity<ApiResponse<ClozeTheme>> getThemeById(@PathVariable Long themeId) {
        logger.info("Getting theme by ID: {}", themeId);
        return clozeService.getThemeById(themeId)
                .map(theme -> ResponseEntity.ok(ApiResponse.success(theme)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/themes")
    @ApiOperation("Create a new theme")
    public ResponseEntity<ApiResponse<ClozeTheme>> createTheme(@RequestBody ClozeTheme theme) {
        logger.info("Creating new theme: {}", theme.getThemeName());
        ClozeTheme savedTheme = clozeService.saveTheme(theme);
        return ResponseEntity.ok(ApiResponse.success(savedTheme));
    }

    @PutMapping("/themes/{themeId}")
    @ApiOperation("Update an existing theme")
    public ResponseEntity<ApiResponse<ClozeTheme>> updateTheme(@PathVariable Long themeId, @RequestBody ClozeTheme theme) {
        logger.info("Updating theme: {}", themeId);
        theme.setThemeId(themeId);
        ClozeTheme updatedTheme = clozeService.saveTheme(theme);
        return ResponseEntity.ok(ApiResponse.success(updatedTheme));
    }

    @DeleteMapping("/themes/{themeId}")
    @ApiOperation("Delete a theme (soft delete)")
    public ResponseEntity<ApiResponse<String>> deleteTheme(@PathVariable Long themeId) {
        logger.info("Deleting theme: {}", themeId);
        clozeService.deleteTheme(themeId);
        return ResponseEntity.ok(ApiResponse.success("Theme deleted successfully"));
    }

    // ===== Passage Endpoints =====

    @GetMapping("/themes/{themeId}/passages")
    @ApiOperation("Get all passages for a theme")
    public ResponseEntity<ApiResponse<List<ClozePassage>>> getPassagesByTheme(@PathVariable Long themeId) {
        logger.info("Getting passages for theme: {}", themeId);
        List<ClozePassage> passages = clozeService.getPassagesByTheme(themeId);
        return ResponseEntity.ok(ApiResponse.success(passages));
    }

    @GetMapping("/themes/{themeId}/passages/full")
    @ApiOperation("Get all passages with blanks for a theme")
    public ResponseEntity<ApiResponse<List<ClozePassage>>> getPassagesWithBlanks(@PathVariable Long themeId) {
        logger.info("Getting passages with blanks for theme: {}", themeId);
        List<ClozePassage> passages = clozeService.getPassagesByThemeWithBlanks(themeId);
        return ResponseEntity.ok(ApiResponse.success(passages));
    }

    @GetMapping("/passages/{passageId}")
    @ApiOperation("Get passage by ID with blanks")
    public ResponseEntity<ApiResponse<ClozePassage>> getPassageById(@PathVariable Long passageId) {
        logger.info("Getting passage by ID: {}", passageId);
        ClozePassage passage = clozeService.getPassageWithBlanks(passageId);
        if (passage != null) {
            return ResponseEntity.ok(ApiResponse.success(passage));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/themes/{themeId}/passages")
    @ApiOperation("Create a new passage for a theme")
    public ResponseEntity<ApiResponse<ClozePassage>> createPassage(@PathVariable Long themeId, @RequestBody ClozePassage passage) {
        logger.info("Creating passage for theme: {}", themeId);
        return clozeService.getThemeById(themeId)
                .map(theme -> {
                    passage.setTheme(theme);
                    ClozePassage savedPassage = clozeService.savePassage(passage);
                    return ResponseEntity.ok(ApiResponse.success(savedPassage));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/passages/{passageId}")
    @ApiOperation("Update an existing passage")
    public ResponseEntity<ApiResponse<ClozePassage>> updatePassage(@PathVariable Long passageId, @RequestBody ClozePassage passage) {
        logger.info("Updating passage: {}", passageId);
        passage.setPassageId(passageId);
        ClozePassage updatedPassage = clozeService.savePassage(passage);
        return ResponseEntity.ok(ApiResponse.success(updatedPassage));
    }

    @DeleteMapping("/passages/{passageId}")
    @ApiOperation("Delete a passage (soft delete)")
    public ResponseEntity<ApiResponse<String>> deletePassage(@PathVariable Long passageId) {
        logger.info("Deleting passage: {}", passageId);
        clozeService.deletePassage(passageId);
        return ResponseEntity.ok(ApiResponse.success("Passage deleted successfully"));
    }

    // ===== Blank Endpoints =====

    @GetMapping("/passages/{passageId}/blanks")
    @ApiOperation("Get all blanks for a passage")
    public ResponseEntity<ApiResponse<List<ClozeBlank>>> getBlanksByPassage(@PathVariable Long passageId) {
        logger.info("Getting blanks for passage: {}", passageId);
        List<ClozeBlank> blanks = clozeService.getBlanksByPassage(passageId);
        return ResponseEntity.ok(ApiResponse.success(blanks));
    }

    @PostMapping("/passages/{passageId}/blanks")
    @ApiOperation("Create a new blank for a passage")
    public ResponseEntity<ApiResponse<ClozeBlank>> createBlank(@PathVariable Long passageId, @RequestBody ClozeBlank blank) {
        logger.info("Creating blank for passage: {}", passageId);
        return clozeService.getPassageById(passageId)
                .map(passage -> {
                    blank.setPassage(passage);
                    ClozeBlank savedBlank = clozeService.saveBlank(blank);
                    return ResponseEntity.ok(ApiResponse.success(savedBlank));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/blanks/{blankId}")
    @ApiOperation("Update an existing blank")
    public ResponseEntity<ApiResponse<ClozeBlank>> updateBlank(@PathVariable Long blankId, @RequestBody ClozeBlank blank) {
        logger.info("Updating blank: {}", blankId);
        blank.setBlankId(blankId);
        ClozeBlank updatedBlank = clozeService.saveBlank(blank);
        return ResponseEntity.ok(ApiResponse.success(updatedBlank));
    }

    @DeleteMapping("/blanks/{blankId}")
    @ApiOperation("Delete a blank (soft delete)")
    public ResponseEntity<ApiResponse<String>> deleteBlank(@PathVariable Long blankId) {
        logger.info("Deleting blank: {}", blankId);
        clozeService.deleteBlank(blankId);
        return ResponseEntity.ok(ApiResponse.success("Blank deleted successfully"));
    }

    // ===== Answer Verification =====

    @PostMapping("/verify")
    @ApiOperation("Verify user's answer for a blank")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyAnswer(
            @RequestParam Long blankId,
            @RequestParam String userAnswer) {
        logger.info("Verifying answer for blank: {}, userAnswer: {}", blankId, userAnswer);

        Map<String, Object> result = new HashMap<>();
        return clozeService.getBlankById(blankId)
                .map(blank -> {
                    boolean isCorrect = blank.getAnswer().equalsIgnoreCase(userAnswer.trim());
                    result.put("correct", isCorrect);
                    result.put("correctAnswer", blank.getAnswer());
                    result.put("answerKo", blank.getAnswerKo());
                    result.put("hint", blank.getHint());
                    return ResponseEntity.ok(ApiResponse.success(result));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
