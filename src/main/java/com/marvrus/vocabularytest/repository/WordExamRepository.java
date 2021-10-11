package com.marvrus.vocabularytest.repository;

import com.marvrus.vocabularytest.model.entity.WordExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordExamRepository extends JpaRepository<WordExam, Long> {
}
