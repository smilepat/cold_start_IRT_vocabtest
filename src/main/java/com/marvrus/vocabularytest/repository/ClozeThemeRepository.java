package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.ClozeTheme;
import com.marvrus.vocabularytest.model.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClozeThemeRepository extends JpaRepository<ClozeTheme, Long> {

    List<ClozeTheme> findByActiveYnOrderByDisplayOrderAsc(YesNo activeYn);

    List<ClozeTheme> findByCategoryAndActiveYnOrderByDisplayOrderAsc(String category, YesNo activeYn);

    @Query("SELECT DISTINCT t.category FROM ClozeTheme t WHERE t.activeYn = :activeYn ORDER BY t.category")
    List<String> findDistinctCategories(YesNo activeYn);

    List<ClozeTheme> findByDifficultyLevelAndActiveYnOrderByDisplayOrderAsc(Integer difficultyLevel, YesNo activeYn);
}
