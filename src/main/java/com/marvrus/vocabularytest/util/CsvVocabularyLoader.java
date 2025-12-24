package com.marvrus.vocabularytest.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.repository.WordRepository;

@Component
public class CsvVocabularyLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CsvVocabularyLoader.class);

    @Value("${app.import-vocab-on-startup:false}")
    private boolean importOnStartup;

    // relative to classpath (resources/)
    @Value("${app.vocab-csv:data/vocabulary_9000_adaptive.csv}")
    private String csvPath;

    @Autowired
    private WordRepository wordRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!importOnStartup) {
            log.info("CsvVocabularyLoader: importOnStartup=false, skipping import");
            return;
        }

        long existing = wordRepository.count();
        if (existing > 0) {
            log.info("CsvVocabularyLoader: word table not empty ({} rows), skipping import", existing);
            return;
        }

        ClassPathResource resource = new ClassPathResource(csvPath);
        if (!resource.exists()) {
            log.warn("CsvVocabularyLoader: CSV not found on classpath: {}", csvPath);
            return;
        }

        List<Word> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; } // skip header
                if (line.trim().isEmpty()) continue;

                // naive CSV split - assumes no embedded commas inside fields
                String[] parts = line.split(",");
                String w = parts.length > 0 ? parts[0].trim().replaceAll("^\"|\"$", "") : null;
                String kor = parts.length > 1 ? parts[1].trim().replaceAll("^\"|\"$", "") : null;
                Integer section = null;
                if (parts.length > 2) {
                    try { section = Integer.valueOf(parts[2].trim().replaceAll("\"", "")); } catch (Exception e) { section = null; }
                }

                if (w == null || w.isEmpty()) continue;

                Word word = new Word();
                word.setWord(w);
                word.setKorean(kor);
                word.setDetailSection(section);
                // initialize IRT params (frequency-based difficulty)
                if (section != null) {
                    double maxRank = 9000.0;
                    double b = (section / maxRank) * 5.0 - 2.5;
                    word.setDifficulty(b);
                } else {
                    word.setDifficulty(null);
                }
                word.setDiscrimination(1.0);
                word.setGuessing(0.25);

                words.add(word);
            }
        }

        if (!words.isEmpty()) {
            wordRepository.saveAll(words);
            log.info("CsvVocabularyLoader: imported {} words from {}", words.size(), csvPath);
        } else {
            log.info("CsvVocabularyLoader: no words parsed from {}", csvPath);
        }
    }
}
