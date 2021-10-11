package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.WordExamDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordExamDetailRepository extends JpaRepository<WordExamDetail, Long> {
    List<WordExamDetail> findAllByWordExamSeqnoOrderByExamOrderAsc(Long wordExamSeqno);

    WordExamDetail findByWordExamSeqnoAndExamOrder(Long wordExamSeqno, Integer examOrder);
}
