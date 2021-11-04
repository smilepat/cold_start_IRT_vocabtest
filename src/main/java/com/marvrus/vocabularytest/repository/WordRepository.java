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

    @Query(value = "select a.* from (select * from word where (word_seqno between ?1 and ?2)) a where a.word_seqno not in (select word_seqno from word_exam_detail where word_exam_seqno = (select max(word_exam_seqno) from word_exam_detail))", nativeQuery = true)
    List<Word> findByWordSeqnoBetween(Long low_idx, Long hi_idx);

    Word findByWord(String word);

    @Query(value = "SELECT max(level) FROM Word")
    int getMaxLevel();

    @Query(value = "SELECT max(wordSeqno) FROM Word")
    Long getMaxWordSeqno();
}
