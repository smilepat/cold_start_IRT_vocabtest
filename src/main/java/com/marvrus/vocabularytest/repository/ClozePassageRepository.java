package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.ClozePassage;
import com.marvrus.vocabularytest.model.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClozePassageRepository extends JpaRepository<ClozePassage, Long> {

    List<ClozePassage> findByThemeThemeIdAndActiveYnOrderByPassageOrderAsc(Long themeId, YesNo activeYn);

    @Query("SELECT p FROM ClozePassage p LEFT JOIN FETCH p.blanks WHERE p.passageId = :passageId AND p.activeYn = :activeYn")
    ClozePassage findByIdWithBlanks(@Param("passageId") Long passageId, @Param("activeYn") YesNo activeYn);

    @Query("SELECT p FROM ClozePassage p LEFT JOIN FETCH p.blanks WHERE p.theme.themeId = :themeId AND p.activeYn = :activeYn ORDER BY p.passageOrder")
    List<ClozePassage> findByThemeIdWithBlanks(@Param("themeId") Long themeId, @Param("activeYn") YesNo activeYn);
}
