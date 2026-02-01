package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.ClozeBlank;
import com.marvrus.vocabularytest.model.enums.YesNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClozeBlankRepository extends JpaRepository<ClozeBlank, Long> {

    List<ClozeBlank> findByPassagePassageIdAndActiveYnOrderByBlankNumberAsc(Long passageId, YesNo activeYn);

    List<ClozeBlank> findByPassagePassageIdOrderByBlankNumberAsc(Long passageId);
}
