package com.marvrus.vocabularytest.service.irt;

import com.marvrus.vocabularytest.model.entity.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 문항 선택기
 * 현재 Theta에서 최적의 문항을 선택하는 전략 구현
 */
@Component
public class ItemSelector {

    private static final Logger logger = LoggerFactory.getLogger(ItemSelector.class);

    @Autowired
    private IrtEngine irtEngine;

    @Value("${calibration.exposure-rate-limit:0.3}")
    private double maxExposureRate;

    @Value("${cat.min-information:0.1}")
    private double minInformation;

    @Value("${cat.top-n-random:5}")
    private int topNRandom;

    /**
     * 최대 정보량 기준 문항 선택 (Maximum Information)
     * 가장 기본적인 CAT 문항 선택 전략
     *
     * @param currentTheta 현재 능력 추정치
     * @param availableItems 사용 가능한 문항 리스트
     * @param usedItemIds 이미 출제된 문항 ID 집합
     * @return 선택된 문항 (없으면 null)
     */
    public Word selectByMaxInfo(double currentTheta,
                                 List<Word> availableItems,
                                 Set<Long> usedItemIds) {
        if (availableItems == null || availableItems.isEmpty()) {
            return null;
        }

        Word bestItem = null;
        double maxInfo = Double.NEGATIVE_INFINITY;

        for (Word item : availableItems) {
            if (usedItemIds != null && usedItemIds.contains(item.getWordSeqno())) {
                continue;
            }

            double info = irtEngine.itemInformation(currentTheta, item);
            if (info > maxInfo) {
                maxInfo = info;
                bestItem = item;
            }
        }

        if (bestItem != null) {
            logger.debug("MaxInfo selection: wordSeqno={}, info={}, theta={}",
                    bestItem.getWordSeqno(),
                    String.format("%.4f", maxInfo),
                    String.format("%.4f", currentTheta));
        }

        return bestItem;
    }

    /**
     * 내용 균형 + 최대 정보량 (Content Balancing)
     * 난이도 구간별 균형 있게 출제
     *
     * @param currentTheta 현재 능력 추정치
     * @param availableItems 사용 가능한 문항 리스트
     * @param usedItemIds 이미 출제된 문항 ID 집합
     * @param contentCounts 내용 영역별 출제 횟수
     * @return 선택된 문항
     */
    public Word selectWithContentBalance(double currentTheta,
                                          List<Word> availableItems,
                                          Set<Long> usedItemIds,
                                          Map<String, Integer> contentCounts) {
        if (availableItems == null || availableItems.isEmpty()) {
            return null;
        }

        // 가장 적게 출제된 내용 영역 찾기
        String targetContent = contentCounts.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 해당 영역에서 최대 정보량 문항 선택
        List<Word> filteredItems = availableItems.stream()
                .filter(item -> usedItemIds == null || !usedItemIds.contains(item.getWordSeqno()))
                .filter(item -> targetContent == null || getContentArea(item).equals(targetContent))
                .collect(Collectors.toList());

        // 해당 영역에 문항이 없으면 전체에서 선택
        if (filteredItems.isEmpty()) {
            filteredItems = availableItems.stream()
                    .filter(item -> usedItemIds == null || !usedItemIds.contains(item.getWordSeqno()))
                    .collect(Collectors.toList());
        }

        return selectByMaxInfo(currentTheta, filteredItems, usedItemIds);
    }

    /**
     * 노출 제어 + 최대 정보량 (Exposure Control)
     * 과다 노출 문항 제한 및 확률적 선택
     *
     * @param currentTheta 현재 능력 추정치
     * @param availableItems 사용 가능한 문항 리스트
     * @param usedItemIds 이미 출제된 문항 ID 집합
     * @return 선택된 문항
     */
    public Word selectWithExposureControl(double currentTheta,
                                           List<Word> availableItems,
                                           Set<Long> usedItemIds) {
        if (availableItems == null || availableItems.isEmpty()) {
            return null;
        }

        // 노출률 필터링
        List<Word> eligibleItems = availableItems.stream()
                .filter(item -> usedItemIds == null || !usedItemIds.contains(item.getWordSeqno()))
                .filter(item -> getExposureRate(item) < maxExposureRate)
                .collect(Collectors.toList());

        // 노출률 제한을 초과한 문항만 있는 경우, 제한 완화
        if (eligibleItems.isEmpty()) {
            eligibleItems = availableItems.stream()
                    .filter(item -> usedItemIds == null || !usedItemIds.contains(item.getWordSeqno()))
                    .collect(Collectors.toList());
        }

        if (eligibleItems.isEmpty()) {
            return null;
        }

        // 최소 정보량 기준 필터링
        List<Word> candidates = new ArrayList<>();
        for (Word item : eligibleItems) {
            double info = irtEngine.itemInformation(currentTheta, item);
            if (info >= minInformation) {
                candidates.add(item);
            }
        }

        // 정보량이 낮은 문항만 있는 경우
        if (candidates.isEmpty()) {
            candidates = eligibleItems;
        }

        // 상위 N개 중 랜덤 선택 (과다노출 방지)
        double finalTheta = currentTheta;
        candidates.sort((a, b) -> Double.compare(
                irtEngine.itemInformation(finalTheta, b),
                irtEngine.itemInformation(finalTheta, a)
        ));

        int topN = Math.min(topNRandom > 0 ? topNRandom : 5, candidates.size());
        Word selected = candidates.get(new Random().nextInt(topN));

        logger.debug("ExposureControl selection: wordSeqno={}, candidates={}, theta={}",
                selected.getWordSeqno(), candidates.size(), String.format("%.4f", currentTheta));

        return selected;
    }

    /**
     * 난이도 기반 초기 문항 선택
     * 시험 시작 시 θ=0에 가장 적합한 문항 선택
     *
     * @param availableItems 사용 가능한 문항 리스트
     * @param targetDifficulty 목표 난이도 (기본: 0.0)
     * @return 선택된 문항
     */
    public Word selectInitialItem(List<Word> availableItems, double targetDifficulty) {
        if (availableItems == null || availableItems.isEmpty()) {
            return null;
        }

        // 목표 난이도에 가장 가까운 문항들 찾기
        List<Word> candidates = availableItems.stream()
                .filter(item -> item.getDifficulty() != null)
                .sorted(Comparator.comparingDouble(item ->
                        Math.abs(item.getDifficulty() - targetDifficulty)))
                .limit(10)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            // 난이도 정보가 없으면 중간 구간 문항 선택
            candidates = availableItems.stream()
                    .filter(item -> item.getDetailSection() >= 4000 && item.getDetailSection() <= 5000)
                    .limit(10)
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            candidates = availableItems;
        }

        // 후보 중 랜덤 선택
        return candidates.get(new Random().nextInt(Math.min(5, candidates.size())));
    }

    /**
     * 내용 영역 판별
     * detailSection 기반으로 난이도 구간 분류
     */
    private String getContentArea(Word item) {
        int section = item.getDetailSection();
        if (section <= 1000) return "초등";
        if (section <= 3000) return "중등";
        if (section <= 6000) return "고등";
        return "고급";
    }

    /**
     * 노출률 계산
     * 문항의 출제 빈도 / 전체 시험 수
     */
    private double getExposureRate(Word item) {
        Integer responseCount = item.getResponseCount();
        if (responseCount == null || responseCount == 0) {
            return 0.0;
        }
        // 전체 시험 수를 10000으로 가정 (실제로는 DB에서 조회해야 함)
        return responseCount / 10000.0;
    }
}
