package com.marvrus.vocabularytest.service.impl;

import com.marvrus.vocabularytest.config.exception.ApiException;
import com.marvrus.vocabularytest.model.WordExamAnswerForm;
import com.marvrus.vocabularytest.model.entity.Word;
import com.marvrus.vocabularytest.model.entity.WordExam;
import com.marvrus.vocabularytest.model.entity.WordExamDetail;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.WordExamDetailRepository;
import com.marvrus.vocabularytest.repository.WordExamRepository;
import com.marvrus.vocabularytest.repository.WordRepository;
import com.marvrus.vocabularytest.service.WordExamService;
import com.marvrus.vocabularytest.utils.LocalDateTimeZoneUtil;
import com.marvrus.vocabularytest.utils.Utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WordExamServiceImpl implements WordExamService {

	private static Logger logger = LoggerFactory.getLogger(WordExamServiceImpl.class);

    private static final int DETAIL_SECTION_BOTTOM = 1;
    private static final int DETAIL_SECTION_MIDDEL = 4500;
    private static final int DETAIL_SECTION_TOP = 9000;

    private final WordRepository wordRepository;
    private final WordExamRepository wordExamRepository;
    private final WordExamDetailRepository wordExamDetailRepository;

    private final int window = 50;

    final class SeqRange {
        private final Long low;
        private final Long high;

        public SeqRange(Long low, Long high) {
            this.low = low;
            this.high = high;
        }

        public Long getLowLimit() {
            return low;
        }

        public Long getHighLimit() {
            return high;
        }
    }

    @Autowired
    public WordExamServiceImpl(WordRepository wordRepository, WordExamRepository wordExamRepository,
                               WordExamDetailRepository wordExamDetailRepository) {
        this.wordRepository = wordRepository;
        this.wordExamRepository = wordExamRepository;
        this.wordExamDetailRepository = wordExamDetailRepository;
    }

    @Override
    @Transactional
    public WordExam generateWordExam() {
        WordExam wordExam = new WordExam();
        wordExam.setExamStartDt(LocalDateTimeZoneUtil.getNow());
        wordExam.setExamDoneYn(YesNo.N);
        wordExam = wordExamRepository.save(wordExam);

        WordExamDetail wordExamDetail = new WordExamDetail();
        wordExamDetail.setWordExamSeqno(wordExam.getWordExamSeqno());
        wordExamDetail.setExamOrder(1);
        // Get words count

        Long words_qty = wordRepository.getMaxWordSeqno();
        logger.error("generateWordExam============words_qty" + words_qty);
        if (words_qty > 0) {
        	wordExamDetail.setWordSeqnoLowLimit(1L);
        	wordExamDetail.setWordSeqnoHighLimit(words_qty);
        }

        SeqRange seqLimit = getRangeSeqno(wordExamDetail.getWordSeqnoLowLimit(), wordExamDetail.getWordSeqnoHighLimit());

        logger.error("============seqLimit.getLowLimit()" + seqLimit.getLowLimit());
        logger.error("============seqLimit.getHighLimit()" + seqLimit.getHighLimit());
        List<Word> wordList = wordRepository.findByWordSeqnoBetween(seqLimit.getLowLimit(), seqLimit.getHighLimit());
        //int start_level = (int) Math.ceil((float)wordRepository.getMaxLevel() / 2);
        //List<Word> wordList = wordRepository.findAllByLevel(start_level);

        //List<Word> wordList = wordRepository.findAllByDetailSection(DETAIL_SECTION_MIDDEL);

        wordExamDetail.setWordSeqno(wordList.get(RandomUtils.nextInt(0, wordList.size())).getWordSeqno());
        wordExamDetailRepository.save(wordExamDetail);

        logger.error("===================================");
        logger.error("Exam Number : " + wordExamDetail.getWordExamSeqno());
        logger.error("Exam Question Index : " + wordExamDetail.getWordSeqno());
        logger.error("Word Index Range : " + wordExamDetail.getWordSeqnoLowLimit() + " - " + wordExamDetail.getWordSeqnoHighLimit());
        logger.error("Chosen word between index : " + seqLimit.getLowLimit() + " - " + seqLimit.getHighLimit());
        logger.error("Words Count : " + wordList.size());
        logger.error("===================================");

        return wordExam;
    }

    private SeqRange getRangeSeqno(Long low_idx, Long high_idx) {
    	Long low_limit = 0L;
    	Long hi_limit = 0L;

    	Long range = (high_idx - low_idx) + 1;

    	logger.error("james high_idx  " + high_idx.toString() + " low_idx  " + low_idx.toString());

        if (range <= window)
        	return new SeqRange(low_idx, high_idx);
        else {
        	// If even then get 50 words
			if (range % 2 == 0) {
			    float mid_value = (float) (high_idx + low_idx) / 2;

			    //james
			    //low_limit = (long) (Math.floor(mid_value) - ((window /2) - 1));
			    low_limit = (long) (Math.floor(mid_value) - ((window /2)));

			    //james changed math.ceil to math.floor
			    // hi_limit =  (long) (Math.floor(mid_value) + ((window /2) - 1));
			    hi_limit =  (long) (Math.floor(mid_value) + ((window /2)));
			}
			// If odd then get 51 words
			else {
				Long mid_value = (high_idx + low_idx) / 2;

				 //james add math.floor
				low_limit = (long) Math.floor(mid_value) - (window /2);
				hi_limit =  (long) Math.floor(mid_value) + (window /2);
			}

        	return new SeqRange(low_limit, hi_limit);
        }
    }

    @Override
    public Word getExamWord(Long wordExamSeqno, Integer examOrder) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.1");
        }

        if (Objects.isNull(examOrder) || examOrder < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.2");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.3");
        }

        if (wordExamResult.get().getExamDoneYn() == YesNo.Y) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 종료된 테스트입니다.4");
        }
       //james error
        logger.error("james error wordExamSeqno1  " + wordExamSeqno.toString() + " examOrder  " + examOrder.toString());
        WordExamDetail wordExamDetail = wordExamDetailRepository
                .findByWordExamSeqnoAndExamOrder(wordExamSeqno, examOrder);
        logger.error("james error wordExamSeqno2  " + wordExamSeqno.toString() + " examOrder  " + examOrder.toString());


        logger.error("james error wordExamSeqno  " + wordExamSeqno.toString() + " examOrder  " + examOrder.toString());
        if (Objects.isNull(wordExamDetail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.5");
        }

        return wordExamDetail.getWord();
    }

    @Override
    @Transactional
    public Map<String, Object> submitAnswer(Long wordExamSeqno, Integer examOrder,
                                            WordExamAnswerForm wordExamAnswerForm) {
        if (Objects.isNull(wordExamSeqno) || Objects.isNull(examOrder) || Objects
                .isNull(wordExamAnswerForm) || wordExamSeqno == 0 || examOrder < 1) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        WordExam wordExam = wordExamResult.get();

        List<WordExamDetail> wordExamDetails = wordExam.getWordExamDetails();
        logger.error("james===wordExamSeqno" + wordExamSeqno.toString());
        logger.error("james===examOrder" + examOrder.toString());
        logger.error("james===wordExamDetails.size()" + wordExamDetails.size());
        if (examOrder != wordExamDetails.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Word targetWord = wordRepository.findByWord(wordExamAnswerForm.getWord());
        if (Objects.isNull(targetWord)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "존재하지 않는 문제입니다.");
        }

        WordExamDetail wordExamDetail = wordExamDetailRepository
                .findByWordExamSeqnoAndExamOrder(wordExamSeqno, examOrder);
        if (Objects.isNull(wordExamDetail)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }
        logger.error("james===wordExamDetail.getWord()" + wordExamDetail.getWord().getWord() + "james===wordExamAnswerForm.getWord()" + wordExamAnswerForm.getWord());
        if (!StringUtils.equals(wordExamDetail.getWord().getWord(), wordExamAnswerForm.getWord())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        wordExamDetail.setCreateDt(LocalDateTimeZoneUtil.getNow());
        wordExamDetail.setAnswer(wordExamAnswerForm.getAnswer());

        // This is used when answer is text input
        //String[] meanings = StringUtils.split(targetWord.getMeaning(), ",");
        // This is used when answer is multiple choice
        String meaning = targetWord.getAnswer();

        logger.error("Got Answer " + wordExamAnswerForm.getAnswer());

        YesNo correctYn = YesNo.N;

        // This is used when answer is text input, where meaning can be have multiple answer
        /*
        for (String meaning : meanings) {
            if (StringUtils.equals(StringUtils.deleteWhitespace(meaning),
                    StringUtils.deleteWhitespace(wordExamAnswerForm.getAnswer()))) {
                correctYn = YesNo.Y;
                break;
            }
        }*/
        // This is used when answer is multiple choice, and only has single answer
        if (StringUtils.equals(StringUtils.deleteWhitespace(meaning),
                StringUtils.deleteWhitespace(wordExamAnswerForm.getAnswer()))) {
            correctYn = YesNo.Y;
        }

        wordExamDetail.setCorrectYn(correctYn);
        wordExamDetailRepository.save(wordExamDetail);

        Map<String, Object> result = new HashMap<>();

        wordExamDetail.setWord(targetWord);
        wordExamDetails.add(wordExamDetail);

        //boolean isExamEnd = getNextDetailSection(wordExamDetails) == targetWord.getDetailSection();

        boolean isExamEnd = false;

        /*
        if (examOrder > 3)
        	isExamEnd = true;
        */

        SeqRange seqRange = getNextRange(wordExamDetails);
        if ((seqRange.getHighLimit() - seqRange.getLowLimit()) <= window) {
        	isExamEnd = true;
        }

        if (!isExamEnd) {
            Word nextWord = getNextWord(wordExamDetails);
            if (Objects.isNull(nextWord)) {
                isExamEnd = true;
            } else {
                WordExamDetail nextWordExam = new WordExamDetail();
                nextWordExam.setWordExamSeqno(wordExam.getWordExamSeqno());
                nextWordExam.setExamOrder(examOrder + 1);
                nextWordExam.setWordSeqno(nextWord.getWordSeqno());
                nextWordExam.setWordSeqnoLowLimit(seqRange.getLowLimit());
            	nextWordExam.setWordSeqnoHighLimit(seqRange.getHighLimit());

                wordExamDetailRepository.save(nextWordExam);

                logger.error("===================================");
	            logger.error("Exam Number : " + nextWordExam.getWordExamSeqno());
	            logger.error("Exam Question Index : " + nextWordExam.getWordSeqno());
	            logger.error("Next Word Index Range : " + nextWordExam.getWordSeqnoLowLimit() + " - " + nextWordExam.getWordSeqnoHighLimit());
	            logger.error("===================================");

            }
        }

        result.put("isExamEnd", isExamEnd);
        return result;
    }

    private Word getNextWord(List<WordExamDetail> wordExamDetails) {
        //List<Word> wordList = wordRepository.findAllByDetailSection(getNextDetailSection(wordExamDetails));
    	//List<Word> wordList = wordRepository.findAllByLevel(getNextLevel(wordExamDetails));
    	SeqRange seqRange = getNextRange(wordExamDetails);
    	SeqRange seqWindow = getRangeSeqno(seqRange.getLowLimit(), seqRange.getHighLimit());
        List<Word> wordList = wordRepository.findByWordSeqnoBetween(seqWindow.getLowLimit(), seqWindow.getHighLimit());

        logger.error("===================================");
        logger.error("Chosen next word between index : " + seqWindow.getLowLimit() + " - " + seqWindow.getHighLimit());
        logger.error("Words Count : " + wordList.size());
        logger.error("===================================");

        Word nextWord = wordList.get(RandomUtils.nextInt(0, wordList.size()));

        for (WordExamDetail beforeExam : wordExamDetails) {
            if (nextWord.getWordSeqno().equals(beforeExam.getWordSeqno())) {
                if (wordList.size() == 1) {
                    return null;
                }
            }
        }

        return nextWord;
    }


    // Is this method ever called ?
    @Override
    public WordExam examDone(Long wordExamSeqno) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        WordExam wordExam = wordExamResult.get();

        List<WordExamDetail> wordExamDetailList = wordExam.getWordExamDetails();
        boolean isTestDone = !Objects.isNull(wordExamDetailList.get(wordExamDetailList.size() - 1).getCorrectYn());
        if (!isTestDone) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        int correctCnt = 0;
        logger.error("==============wordExamDetailList*****" + wordExamDetailList.toString());
        for (WordExamDetail wordExamDetail : wordExamDetailList) {
            if (wordExamDetail.getCorrectYn() == YesNo.Y) {
                correctCnt++;
            }

            if (isTestDone) {
                // Uncommented because we won't use getNextDetailSection method any longer

            	//int nextDetailSection = getNextDetailSection(wordExamDetailList);
                //wordExam.setExamDetailSection(nextDetailSection);
                //wordExam.setExamLevel((nextDetailSection - 1) / 10 + 1);
            	logger.error("==============wordExamDetailList*****" + wordExamDetailList.toString());
            	int nextDetailSection = getNextDetailSection(wordExamDetailList);
                wordExam.setExamDetailSection(nextDetailSection);
                wordExam.setExamLevel((nextDetailSection - 1) / 10 + 1);
                logger.error("==============setExamLevel*****" + (nextDetailSection - 1) / 10 + 1);
            }
        }

        wordExam.setScore(correctCnt);
        wordExam.setExamDoneYn(YesNo.Y);
        wordExam.setExamEndDt(LocalDateTimeZoneUtil.getNow());
        return wordExamRepository.save(wordExam);
    }


    private int getNextDetailSection(List<WordExamDetail> wordExamDetails) {
        double highest = DETAIL_SECTION_TOP;
        double lowest = DETAIL_SECTION_BOTTOM;

        for (WordExamDetail wordExamDetail : wordExamDetails) {
            if (wordExamDetail.getCorrectYn() == YesNo.Y) {
                lowest = wordExamDetail.getWord().getDetailSection();
            } else {
                highest = wordExamDetail.getWord().getDetailSection();
            }
        }
        logger.error("===highest*****" + highest + "===lowest*****" +  lowest);
        return (int) Math.floor((lowest + highest) / 2);
    }


    //private int getNextLevel(List<WordExamDetail> wordExamDetails) {
    	/* getNextWord - Logic Part
    	 * If at the current level there are 3 true from 5 questions,
    	 * next word is fetched from higher level
    	 * If at the current level there are 3 false from 5 questions,
    	 * next word is fetched from lower level
    	 *
    	 * Additional rules :
    	 * If from 3 question user already got 2 true, can go to next level
    	 */

    /*
		logger.error("===================================");

		int currLevel = wordExamDetails.get(wordExamDetails.size() - 1).getWord().getLevel(); // For return value
		final int level = currLevel; // For filter use, should be final

		// LastN using 6 because the last index is duplicated in the list
		// Will fetch last 6 of the same level only
		List<WordExamDetail> lastFive = wordExamDetails.stream().collect(Utils.lastN(6));
		// Remove the duplicated list member, so now the member will be 5
		lastFive.remove(lastFive.size() - 1);
		// Filter for same level only
		lastFive = lastFive.stream().filter(obj -> level == obj.getWord().getLevel()).collect(Collectors.toList());

		// Another filter in case member are same level but not in the right order
		int index = 0;
		int overlappingIndex = -1;
		int lastExamOrder = 0;
		for ( WordExamDetail elem : lastFive ) {
			if (index == 0)
				lastExamOrder = elem.getExamOrder();
			else {
				//logger.error("Element : " + elem.getExamOrder());
				//logger.error("Element : " + (lastExamOrder + 1));

				if (elem.getExamOrder() != (lastExamOrder + 1))
					overlappingIndex = index;

				lastExamOrder = elem.getExamOrder();
			}

			index++;
		}
		// Unordered list detected
		// We only take the latest list with same level with correct order
		if (overlappingIndex != -1) {
			lastFive = wordExamDetails.stream().collect(Utils.lastN(lastFive.size() - overlappingIndex));
		}

		int correctCount = 0; // Counting correct results
		for ( WordExamDetail elem : lastFive ) {
			logger.error("Element : " + elem.getExamOrder());
			logger.error("Element : " + elem.getWord().getWord());
			logger.error("Element : " + elem.getWord().getLevel());
			logger.error("Element : " + elem.getCorrectYn());

			if (elem.getCorrectYn() == YesNo.Y) {
				correctCount++;
            }
		}

		logger.error("Correct Count : " + correctCount + "/" + lastFive.size());
		logger.error("===================================");

		// Check if at least 2 correct from 3 questions
		if (lastFive.size() == 3) {

			// Increase level if 2/3
			if (correctCount >= 2) {
				currLevel++;
				logger.error("Increase Level : " + currLevel);
			}
			// Decrease level if 0/3
			else if (correctCount == 0) {
				currLevel--;
				logger.error("Decrease Level : " + currLevel);
			}
			else {
				logger.error("Same Level : " + currLevel);
			}

		} else if (lastFive.size() == 4) {
			// Decrease level if 1/4
			if (correctCount == 1) {
				currLevel--;
				logger.error("Decrease Level : " + currLevel);
			}
		} else {
			// Check if at least 3 correct from 5 questions
			if (lastFive.size() == 5) {

				if (correctCount >= 3) {
					currLevel++;
					logger.error("Increase Level : " + currLevel);
				} else {
					currLevel--;
					logger.error("Decrease Level : " + currLevel);
				}

			} else {
				logger.error("Same Level : " + currLevel);
			}
		}

		return currLevel;
    }
    */

    private SeqRange getNextRange(List<WordExamDetail> wordExamDetails) {
    	/* getNextRange - Logic Part
    	 * If at the current level there are 3 true from 5 questions,
    	 * next word is fetched from higher level
    	 * If at the current level there are 3 false from 5 questions,
    	 * next word is fetched from lower level
    	 *
    	 * Additional rules :
    	 * If from 3 question user already got 2 true, can go to next level
    	 */
    	WordExamDetail latestExam = wordExamDetails.get(wordExamDetails.size() - 1);
    	Long latest_low = latestExam.getWordSeqnoLowLimit();
    	Long latest_hi = latestExam.getWordSeqnoHighLimit();

    	logger.error("james latest_low" + latest_low.toString() + "james latest_hi" + latest_hi.toString());

    	Long sum = latest_low + latest_hi;

    	logger.error("james sum" + sum.toString());

    	Long new_low = 0L;
    	Long new_hi = 0L;

		// Get current limit
		Long currLowLimit = wordExamDetails.get(wordExamDetails.size() - 1).getWordSeqnoLowLimit();
		Long currHighLimit = wordExamDetails.get(wordExamDetails.size() - 1).getWordSeqnoHighLimit();

		logger.error("james currLowLimit" + currLowLimit.toString() + "james currHighLimit" + currHighLimit.toString());

		// LastN using 6 because the last index is duplicated in the list
		// Will fetch last 6 of the same range only
		List<WordExamDetail> lastFive = wordExamDetails.stream().collect(Utils.lastN(6));
		// Remove the duplicated list member, so now the member will be 5
		lastFive.remove(lastFive.size() - 1);
		// Filter for same range only
		lastFive = lastFive.stream()
				.filter(obj -> currLowLimit.equals(obj.getWordSeqnoLowLimit()) && currHighLimit.equals(obj.getWordSeqnoHighLimit()))
				.collect(Collectors.toList());

		int correctCount = 0; // Counting correct results
		for ( WordExamDetail elem : lastFive ) {
			logger.error("Order Index : " + elem.getExamOrder());
			logger.error("Words : " + elem.getWord().getWord());
			logger.error("Correct : " + elem.getCorrectYn());
			logger.error("Low Limit : " + elem.getWordSeqnoLowLimit());
			logger.error("High Limit : " + elem.getWordSeqnoHighLimit());

			if (elem.getCorrectYn() == YesNo.Y) {
				correctCount++;
            }
		}

		logger.error("Correct Count : " + correctCount + "/" + lastFive.size());
		logger.error("===================================");

		boolean incDifficulty = false;
		boolean decDifficulty = false;
		// Check if at least 2 correct from 3 questions
		if (lastFive.size() == 3) {

			// Increase level if 2/3
			if (correctCount >= 3) {
				incDifficulty = true;
				logger.error("Increase Difficulty");
			}
			// Decrease level if 0/3
			else if (correctCount == 0) {
				decDifficulty = true;
				logger.error("Decrease Difficulty");
			}
			else {
				logger.error("Same Difficulty");
			}

		} else if (lastFive.size() == 4) {
			// Decrease level if 1/4
			if (correctCount == 1) {
				decDifficulty = true;
				logger.error("Decrease Difficulty");
			}
		} else {
			// Check if at least 3 correct from 5 questions
			if (lastFive.size() == 5) {

				if (correctCount >= 3) {
					incDifficulty = true;
					logger.error("Increase Difficulty");
				} else {
					decDifficulty = true;
					logger.error("Decrease Difficulty");
				}

			} else {
				logger.error("Same Difficulty");
			}
		}

    	if (incDifficulty && !decDifficulty) {
    		new_hi = latest_hi;
    		new_low = (long) Math.ceil((float) sum / 2);
    	}
    	else if (!incDifficulty && decDifficulty) {
    		new_low = latest_low;
    		new_hi = (long) Math.floor((float) sum / 2);
    	}
    	else if (!incDifficulty && !decDifficulty) {
    		new_hi = latest_hi;
    		new_low = latest_low;
    	} else {
    		new_hi = latest_hi;
    		new_low = latest_low;

    		logger.error("================================================");
    		logger.error("Check getNextRange method, should not reach here");
    		logger.error("================================================");
    	}

    	return new SeqRange(new_low, new_hi);
    }

    @Override
    public WordExam getWordExamProgress(Long wordExamSeqno) {
        if (Objects.isNull(wordExamSeqno) || wordExamSeqno == 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        Optional<WordExam> wordExamResult = wordExamRepository.findById(wordExamSeqno);
        logger.error("james=====wordExamResult.isPresent()" + wordExamResult.isPresent());
        if (!wordExamResult.isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        return wordExamResult.get();
    }

    @Override
    public List<Word> getWordCard(Long lowSeqno, Long highSeqno) {
        if (Objects.isNull(lowSeqno) || Objects.isNull(highSeqno)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        List<Word> words = wordRepository.findByWordSeqnoBetween(lowSeqno, highSeqno);
        return words;
    }
}
