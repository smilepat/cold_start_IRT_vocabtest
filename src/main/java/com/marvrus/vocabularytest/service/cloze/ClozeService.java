package com.marvrus.vocabularytest.service.cloze;

import com.marvrus.vocabularytest.model.entity.ClozeBlank;
import com.marvrus.vocabularytest.model.entity.ClozePassage;
import com.marvrus.vocabularytest.model.entity.ClozeTheme;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.ClozeBlankRepository;
import com.marvrus.vocabularytest.repository.ClozePassageRepository;
import com.marvrus.vocabularytest.repository.ClozeThemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClozeService {

    @Autowired
    private ClozeThemeRepository themeRepository;

    @Autowired
    private ClozePassageRepository passageRepository;

    @Autowired
    private ClozeBlankRepository blankRepository;

    // ===== Theme Methods =====

    public List<ClozeTheme> getAllActiveThemes() {
        return themeRepository.findByActiveYnOrderByDisplayOrderAsc(YesNo.Y);
    }

    public List<ClozeTheme> getThemesByCategory(String category) {
        return themeRepository.findByCategoryAndActiveYnOrderByDisplayOrderAsc(category, YesNo.Y);
    }

    public List<ClozeTheme> getThemesByDifficulty(Integer difficultyLevel) {
        return themeRepository.findByDifficultyLevelAndActiveYnOrderByDisplayOrderAsc(difficultyLevel, YesNo.Y);
    }

    public List<String> getAllCategories() {
        return themeRepository.findDistinctCategories(YesNo.Y);
    }

    public Optional<ClozeTheme> getThemeById(Long themeId) {
        return themeRepository.findById(themeId);
    }

    public ClozeTheme saveTheme(ClozeTheme theme) {
        if (theme.getThemeId() == null) {
            theme.setCreateDt(LocalDateTime.now());
        }
        theme.setUpdateDt(LocalDateTime.now());
        return themeRepository.save(theme);
    }

    public void deleteTheme(Long themeId) {
        themeRepository.findById(themeId).ifPresent(theme -> {
            theme.setActiveYn(YesNo.N);
            theme.setUpdateDt(LocalDateTime.now());
            themeRepository.save(theme);
        });
    }

    // ===== Passage Methods =====

    public List<ClozePassage> getPassagesByTheme(Long themeId) {
        return passageRepository.findByThemeThemeIdAndActiveYnOrderByPassageOrderAsc(themeId, YesNo.Y);
    }

    public List<ClozePassage> getPassagesByThemeWithBlanks(Long themeId) {
        return passageRepository.findByThemeIdWithBlanks(themeId, YesNo.Y);
    }

    public Optional<ClozePassage> getPassageById(Long passageId) {
        return passageRepository.findById(passageId);
    }

    public ClozePassage getPassageWithBlanks(Long passageId) {
        return passageRepository.findByIdWithBlanks(passageId, YesNo.Y);
    }

    public ClozePassage savePassage(ClozePassage passage) {
        if (passage.getPassageId() == null) {
            passage.setCreateDt(LocalDateTime.now());
        }
        passage.setUpdateDt(LocalDateTime.now());
        return passageRepository.save(passage);
    }

    public void deletePassage(Long passageId) {
        passageRepository.findById(passageId).ifPresent(passage -> {
            passage.setActiveYn(YesNo.N);
            passage.setUpdateDt(LocalDateTime.now());
            passageRepository.save(passage);
        });
    }

    // ===== Blank Methods =====

    public List<ClozeBlank> getBlanksByPassage(Long passageId) {
        return blankRepository.findByPassagePassageIdAndActiveYnOrderByBlankNumberAsc(passageId, YesNo.Y);
    }

    public Optional<ClozeBlank> getBlankById(Long blankId) {
        return blankRepository.findById(blankId);
    }

    public ClozeBlank saveBlank(ClozeBlank blank) {
        if (blank.getBlankId() == null) {
            blank.setCreateDt(LocalDateTime.now());
        }
        blank.setUpdateDt(LocalDateTime.now());
        return blankRepository.save(blank);
    }

    public void deleteBlank(Long blankId) {
        blankRepository.findById(blankId).ifPresent(blank -> {
            blank.setActiveYn(YesNo.N);
            blank.setUpdateDt(LocalDateTime.now());
            blankRepository.save(blank);
        });
    }

    // ===== Bulk Save Methods =====

    public ClozeTheme saveThemeWithPassagesAndBlanks(ClozeTheme theme) {
        theme = saveTheme(theme);

        for (ClozePassage passage : theme.getPassages()) {
            passage.setTheme(theme);
            passage = savePassage(passage);

            for (ClozeBlank blank : passage.getBlanks()) {
                blank.setPassage(passage);
                saveBlank(blank);
            }
        }

        return theme;
    }
}
