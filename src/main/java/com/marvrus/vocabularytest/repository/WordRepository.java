package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByDetailSection(int detailSectionMiddel);
    List<Word> findAllByLevel(int level);

    Word findByWord(String word);

    @Query(value = "SELECT max(level) FROM Word")
    int getMaxLevel();
}
