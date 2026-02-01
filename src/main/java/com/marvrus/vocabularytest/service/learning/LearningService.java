package com.marvrus.vocabularytest.service.learning;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvrus.vocabularytest.model.dto.learning.LearningAnswerResult;
import com.marvrus.vocabularytest.model.dto.learning.LearningQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningService {

    private static final Logger log = LoggerFactory.getLogger(LearningService.class);

    private static final Map<String, String> QUESTION_TYPE_LABELS = new LinkedHashMap<>();
    static {
        QUESTION_TYPE_LABELS.put("pictureMatch", "그림+한글뜻 매칭");
        QUESTION_TYPE_LABELS.put("listeningKorean", "듣기+한글뜻");
        QUESTION_TYPE_LABELS.put("spelling", "철자 맞추기");
        QUESTION_TYPE_LABELS.put("contextKorean", "문맥 속 한글 뜻");
        QUESTION_TYPE_LABELS.put("contextEnglish", "문맥 속 영어 뜻");
        QUESTION_TYPE_LABELS.put("fillInBlanks", "객관식 문장완성");
        QUESTION_TYPE_LABELS.put("synonyms", "유의어 찾기");
        QUESTION_TYPE_LABELS.put("antonyms", "반의어 찾기");
        QUESTION_TYPE_LABELS.put("meaningFromContext", "문맥 속 의미 추론");
        QUESTION_TYPE_LABELS.put("collocation", "콜로케이션");
        QUESTION_TYPE_LABELS.put("phonics", "음소(Phonics)");
    }

    /** Level → recommended question types */
    private static final Map<Integer, List<String>> LEVEL_TYPE_MAP = new HashMap<>();
    static {
        // Level 1~3 (A1, 초등): basic types
        List<String> basicTypes = Arrays.asList(
            "pictureMatch", "listeningKorean", "spelling", "contextKorean"
        );
        LEVEL_TYPE_MAP.put(1, basicTypes);
        LEVEL_TYPE_MAP.put(2, basicTypes);
        LEVEL_TYPE_MAP.put(3, basicTypes);

        // Level 4~6 (A2~B1, 중등): intermediate types
        List<String> intermediateTypes = Arrays.asList(
            "contextKorean", "contextEnglish", "fillInBlanks", "spelling", "synonyms"
        );
        LEVEL_TYPE_MAP.put(4, intermediateTypes);
        LEVEL_TYPE_MAP.put(5, intermediateTypes);
        LEVEL_TYPE_MAP.put(6, intermediateTypes);

        // Level 7~9 (B1~B2, 고등): advanced types
        List<String> advancedTypes = Arrays.asList(
            "synonyms", "antonyms", "meaningFromContext", "collocation", "contextEnglish", "fillInBlanks"
        );
        LEVEL_TYPE_MAP.put(7, advancedTypes);
        LEVEL_TYPE_MAP.put(8, advancedTypes);
        LEVEL_TYPE_MAP.put(9, advancedTypes);
    }

    /** Level → CEFR mapping */
    private static final Map<Integer, List<String>> LEVEL_CEFR_MAP = new HashMap<>();
    static {
        LEVEL_CEFR_MAP.put(1, Arrays.asList("A1"));
        LEVEL_CEFR_MAP.put(2, Arrays.asList("A1"));
        LEVEL_CEFR_MAP.put(3, Arrays.asList("A1", "A2"));
        LEVEL_CEFR_MAP.put(4, Arrays.asList("A2"));
        LEVEL_CEFR_MAP.put(5, Arrays.asList("A2", "B1"));
        LEVEL_CEFR_MAP.put(6, Arrays.asList("B1"));
        LEVEL_CEFR_MAP.put(7, Arrays.asList("B1", "B2"));
        LEVEL_CEFR_MAP.put(8, Arrays.asList("B2"));
        LEVEL_CEFR_MAP.put(9, Arrays.asList("B2", "C1"));
    }

    private JsonNode rootData;
    private List<JsonNode> wordList;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("data/learning_questions.json");
            InputStream is = resource.getInputStream();
            rootData = mapper.readTree(is);
            JsonNode wordsNode = rootData.get("words");
            wordList = new ArrayList<>();
            if (wordsNode != null && wordsNode.isArray()) {
                wordsNode.forEach(wordList::add);
            }
            log.info("LearningService: Loaded {} words from learning_questions.json", wordList.size());
        } catch (Exception e) {
            log.error("LearningService: Failed to load learning_questions.json", e);
            wordList = new ArrayList<>();
        }
    }

    /**
     * Get learning questions for a given level
     */
    public List<LearningQuestion> getQuestions(int level, String type, int count) {
        List<String> targetCefrs = LEVEL_CEFR_MAP.getOrDefault(level, Arrays.asList("A1"));
        List<String> targetTypes = (type != null && !type.isEmpty())
            ? Collections.singletonList(type)
            : LEVEL_TYPE_MAP.getOrDefault(level, Arrays.asList("contextKorean"));

        // Filter words by CEFR level
        List<JsonNode> filtered = wordList.stream()
            .filter(w -> {
                String cefr = w.has("cefr") ? w.get("cefr").asText("") : "";
                return targetCefrs.contains(cefr);
            })
            .filter(w -> {
                // Ensure word has at least one of the target question types
                JsonNode questions = w.get("questions");
                if (questions == null) return false;
                return targetTypes.stream().anyMatch(t -> questions.has(t) && !questions.get(t).isNull());
            })
            .collect(Collectors.toList());

        // If not enough words at target CEFR, use all available
        if (filtered.size() < count) {
            filtered = wordList.stream()
                .filter(w -> {
                    JsonNode questions = w.get("questions");
                    if (questions == null) return false;
                    return targetTypes.stream().anyMatch(t -> questions.has(t) && !questions.get(t).isNull());
                })
                .collect(Collectors.toList());
        }

        // Shuffle and take count
        Collections.shuffle(filtered, random);
        List<JsonNode> selected = filtered.subList(0, Math.min(count, filtered.size()));

        // Build LearningQuestion list
        List<LearningQuestion> result = new ArrayList<>();
        for (JsonNode wordNode : selected) {
            // Pick a random question type from available target types
            JsonNode questions = wordNode.get("questions");
            List<String> availableTypes = targetTypes.stream()
                .filter(t -> questions.has(t) && !questions.get(t).isNull())
                .collect(Collectors.toList());

            if (availableTypes.isEmpty()) continue;

            String selectedType = availableTypes.get(random.nextInt(availableTypes.size()));
            JsonNode qNode = questions.get(selectedType);

            LearningQuestion lq = new LearningQuestion();
            lq.setWord(wordNode.get("word").asText(""));
            lq.setPos(wordNode.has("pos") ? wordNode.get("pos").asText("") : "");
            lq.setKoreanDef(wordNode.has("koreanDef") ? wordNode.get("koreanDef").asText("") : "");
            lq.setEnglishDef(wordNode.has("englishDef") ? wordNode.get("englishDef").asText("") : "");
            lq.setExample(wordNode.has("example") ? wordNode.get("example").asText("") : "");
            lq.setCefr(wordNode.has("cefr") ? wordNode.get("cefr").asText("") : "");
            lq.setCurriculum(wordNode.has("curriculum") ? wordNode.get("curriculum").asText("") : "");
            lq.setQuestionType(selectedType);
            lq.setQuestionTypeLabel(QUESTION_TYPE_LABELS.getOrDefault(selectedType, selectedType));
            lq.setQuestion(qNode.has("question") ? qNode.get("question").asText("") : "");
            lq.setAnswer(qNode.has("answer") ? qNode.get("answer").asText("") : "");

            // Parse choices
            List<String> choices = new ArrayList<>();
            List<String> choiceLabels = new ArrayList<>();
            if (qNode.has("choices") && qNode.get("choices").isArray()) {
                qNode.get("choices").forEach(c -> choices.add(c.asText("")));
            }
            if (qNode.has("choiceLabels") && qNode.get("choiceLabels").isArray()) {
                qNode.get("choiceLabels").forEach(c -> choiceLabels.add(c.asText("")));
            }
            lq.setChoices(choices);
            lq.setChoiceLabels(choiceLabels);

            result.add(lq);
        }

        return result;
    }

    /**
     * Check user answer
     */
    public LearningAnswerResult checkAnswer(String word, String questionType, String userAnswer) {
        LearningAnswerResult result = new LearningAnswerResult();
        result.setWord(word);

        // Find the word
        Optional<JsonNode> wordOpt = wordList.stream()
            .filter(w -> w.get("word").asText("").equalsIgnoreCase(word))
            .findFirst();

        if (!wordOpt.isPresent()) {
            result.setCorrect(false);
            result.setCorrectAnswer("unknown");
            return result;
        }

        JsonNode wordNode = wordOpt.get();
        result.setKoreanDef(wordNode.has("koreanDef") ? wordNode.get("koreanDef").asText("") : "");
        result.setEnglishDef(wordNode.has("englishDef") ? wordNode.get("englishDef").asText("") : "");
        result.setExample(wordNode.has("example") ? wordNode.get("example").asText("") : "");

        JsonNode questions = wordNode.get("questions");
        if (questions == null || !questions.has(questionType)) {
            result.setCorrect(false);
            result.setCorrectAnswer("unknown");
            return result;
        }

        JsonNode qNode = questions.get(questionType);
        String correctAnswer = qNode.has("answer") ? qNode.get("answer").asText("") : "";
        result.setCorrectAnswer(correctAnswer);
        result.setCorrect(userAnswer != null && userAnswer.trim().equalsIgnoreCase(correctAnswer.trim()));

        return result;
    }

    /**
     * Get available question types for a level
     */
    public List<Map<String, String>> getAvailableTypes(int level) {
        List<String> types = LEVEL_TYPE_MAP.getOrDefault(level, new ArrayList<>(QUESTION_TYPE_LABELS.keySet()));
        return types.stream()
            .map(t -> {
                Map<String, String> m = new HashMap<>();
                m.put("type", t);
                m.put("label", QUESTION_TYPE_LABELS.getOrDefault(t, t));
                return m;
            })
            .collect(Collectors.toList());
    }

    public int getTotalWordCount() {
        return wordList.size();
    }
}
