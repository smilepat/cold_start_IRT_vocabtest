package com.marvrus.vocabularytest.service.impl;

import com.marvrus.vocabularytest.config.exception.ApiException;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.WordRepository;
import com.marvrus.vocabularytest.utils.LocalDateTimeZoneUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
class WordExamServiceImplTest {
    private WordRepository wordRepository;

    @Autowired
    public WordExamServiceImplTest(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    @Test
    public void test() throws Exception {
        File file = FileUtils.getFile("/Users/marvrus/Documents", "sample.xlsx");
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        if (Objects.isNull(workbook)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "파일이 잘못되었습니다.");
        }
        Sheet sheet = workbook.getSheetAt(0);

        List<Word> wordList = new ArrayList<>();
        for (int index = 3, size = sheet.getLastRowNum(); index < size; index++) {
            Row row = sheet.getRow(index);
            if (Objects.isNull(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))) {
                break;
            }


            Word word = new Word();
            word.setActiveYn(YesNo.Y);
            word.setCreateDt(LocalDateTimeZoneUtil.getNow());
            word.setUpdateDt(LocalDateTimeZoneUtil.getNow());
            word.setWord(row.getCell(1).getStringCellValue());

            List<String> meanings = new ArrayList<>();
            for (int cellIndex = 3, rowSize = row.getLastCellNum(); cellIndex < rowSize; cellIndex++) {
                Cell meaningCell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (Objects.isNull(meaningCell)) {
                    break;
                }

                meanings.add(meaningCell.getStringCellValue());
            }

            word.setMeaning(StringUtils.join(meanings, ","));
            word.setExampleSentence(row.getCell(2).getStringCellValue());
            word.setLevel((index - 3) / 10 + 1);
            word.setDetailSection(index - 2);
            wordList.add(word);
        }

        wordRepository.saveAll(wordList);
    }

}
