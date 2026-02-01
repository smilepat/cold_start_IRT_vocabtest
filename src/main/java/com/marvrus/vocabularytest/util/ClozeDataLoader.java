package com.marvrus.vocabularytest.util;

import com.marvrus.vocabularytest.model.entity.ClozeBlank;
import com.marvrus.vocabularytest.model.entity.ClozePassage;
import com.marvrus.vocabularytest.model.entity.ClozeTheme;
import com.marvrus.vocabularytest.model.enums.YesNo;
import com.marvrus.vocabularytest.repository.ClozeBlankRepository;
import com.marvrus.vocabularytest.repository.ClozePassageRepository;
import com.marvrus.vocabularytest.repository.ClozeThemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Cloze 학습 Mockup 데이터 로더
 * 앱 시작 시 샘플 데이터를 자동으로 로드합니다.
 */
@Component
@Order(2)
public class ClozeDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ClozeDataLoader.class);

    @Value("${cloze.data.import-on-startup:true}")
    private boolean importOnStartup;

    @Autowired
    private ClozeThemeRepository themeRepository;

    @Autowired
    private ClozePassageRepository passageRepository;

    @Autowired
    private ClozeBlankRepository blankRepository;

    @Override
    public void run(String... args) {
        if (!importOnStartup) {
            logger.info("ClozeDataLoader: importOnStartup=false, skipping import");
            return;
        }

        // 이미 데이터가 있으면 스킵
        if (themeRepository.count() > 0) {
            logger.info("ClozeDataLoader: Data already exists, skipping import");
            return;
        }

        logger.info("ClozeDataLoader: Starting mockup data import...");
        loadMockupData();
        logger.info("ClozeDataLoader: Mockup data import completed!");
    }

    private void loadMockupData() {
        // Theme 1: Business Meeting
        ClozeTheme businessTheme = createTheme(
            "Business Meeting",
            "비즈니스 회의",
            "Learn vocabulary used in professional business meetings and corporate environments.",
            3,
            "Business",
            1
        );
        businessTheme = themeRepository.save(businessTheme);

        // Business Meeting - Passage 1
        ClozePassage businessPassage1 = createPassage(
            businessTheme,
            "Project Status Update",
            "Good morning everyone. Let's begin our weekly {{1}} meeting. First, I'd like to discuss the {{2}} of our current project. " +
            "The development team has made significant {{3}} this week. However, we're facing some {{4}} with the timeline. " +
            "We need to {{5}} our resources more effectively to meet the deadline.",
            "안녕하세요 여러분. 주간 상태 회의를 시작하겠습니다. 먼저, 현재 프로젝트의 현황에 대해 논의하고 싶습니다. " +
            "개발팀이 이번 주에 상당한 진전을 이루었습니다. 하지만 일정과 관련된 몇 가지 어려움에 직면해 있습니다. " +
            "마감일을 맞추기 위해 자원을 더 효과적으로 배분해야 합니다.",
            1
        );
        businessPassage1 = passageRepository.save(businessPassage1);

        createAndSaveBlank(businessPassage1, 1, "status", "상태", "Current condition or situation", "meeting", "budget", "schedule", "noun");
        createAndSaveBlank(businessPassage1, 2, "progress", "진행 상황", "Forward movement toward a goal", "problem", "expense", "delay", "noun");
        createAndSaveBlank(businessPassage1, 3, "progress", "진전", "Advancement or development", "mistakes", "complaints", "issues", "noun");
        createAndSaveBlank(businessPassage1, 4, "challenges", "어려움", "Difficulties or obstacles", "benefits", "opportunities", "successes", "noun");
        createAndSaveBlank(businessPassage1, 5, "allocate", "배분하다", "To distribute for a specific purpose", "waste", "ignore", "reduce", "verb");

        // Business Meeting - Passage 2
        ClozePassage businessPassage2 = createPassage(
            businessTheme,
            "Budget Discussion",
            "Now, let's move on to the {{1}} review. Our quarterly {{2}} shows that we are within budget. " +
            "However, we need to {{3}} some funds for the new marketing {{4}}. " +
            "I {{5}} that we reallocate 10% from the operations budget.",
            "이제 예산 검토로 넘어가겠습니다. 분기별 보고서에 따르면 예산 범위 내에 있습니다. " +
            "그러나 새로운 마케팅 캠페인을 위해 일부 자금을 확보해야 합니다. " +
            "운영 예산에서 10%를 재배분할 것을 제안합니다.",
            2
        );
        businessPassage2 = passageRepository.save(businessPassage2);

        createAndSaveBlank(businessPassage2, 1, "budget", "예산", "Financial plan for income and expenses", "meeting", "project", "team", "noun");
        createAndSaveBlank(businessPassage2, 2, "report", "보고서", "Official document with information", "email", "call", "visit", "noun");
        createAndSaveBlank(businessPassage2, 3, "secure", "확보하다", "To obtain or guarantee", "lose", "waste", "spend", "verb");
        createAndSaveBlank(businessPassage2, 4, "campaign", "캠페인", "Organized course of action", "problem", "issue", "mistake", "noun");
        createAndSaveBlank(businessPassage2, 5, "propose", "제안하다", "To suggest for consideration", "demand", "refuse", "reject", "verb");


        // Theme 2: Travel
        ClozeTheme travelTheme = createTheme(
            "Travel & Tourism",
            "여행",
            "Essential vocabulary for traveling, booking accommodations, and exploring new places.",
            2,
            "Daily Life",
            2
        );
        travelTheme = themeRepository.save(travelTheme);

        // Travel - Passage 1
        ClozePassage travelPassage1 = createPassage(
            travelTheme,
            "Hotel Check-in",
            "Welcome to the Grand Hotel. Do you have a {{1}}? May I see your {{2}}, please? " +
            "Your room is on the fifth {{3}}. Here is your key card. The {{4}} is served from 7 to 10 AM. " +
            "If you need any {{5}}, please contact the front desk.",
            "그랜드 호텔에 오신 것을 환영합니다. 예약하셨나요? 신분증을 보여주시겠어요? " +
            "방은 5층에 있습니다. 여기 키 카드가 있습니다. 조식은 오전 7시부터 10시까지 제공됩니다. " +
            "도움이 필요하시면 프론트 데스크로 연락해 주세요.",
            1
        );
        travelPassage1 = passageRepository.save(travelPassage1);

        createAndSaveBlank(travelPassage1, 1, "reservation", "예약", "Arrangement made in advance", "question", "problem", "complaint", "noun");
        createAndSaveBlank(travelPassage1, 2, "identification", "신분증", "Document proving who you are", "money", "luggage", "ticket", "noun");
        createAndSaveBlank(travelPassage1, 3, "floor", "층", "Level of a building", "door", "window", "wall", "noun");
        createAndSaveBlank(travelPassage1, 4, "breakfast", "조식", "Morning meal", "dinner", "lunch", "snack", "noun");
        createAndSaveBlank(travelPassage1, 5, "assistance", "도움", "Help or support", "money", "food", "room", "noun");

        // Travel - Passage 2
        ClozePassage travelPassage2 = createPassage(
            travelTheme,
            "At the Airport",
            "Please have your {{1}} and boarding pass ready. Place your {{2}} on the conveyor belt. " +
            "Remove any {{3}} items from your bag. The {{4}} time is 30 minutes before departure. " +
            "Your flight will {{5}} from Gate 15.",
            "여권과 탑승권을 준비해 주세요. 수하물을 컨베이어 벨트에 올려주세요. " +
            "가방에서 액체류를 꺼내주세요. 탑승 시간은 출발 30분 전입니다. " +
            "귀하의 항공편은 15번 게이트에서 출발합니다.",
            2
        );
        travelPassage2 = passageRepository.save(travelPassage2);

        createAndSaveBlank(travelPassage2, 1, "passport", "여권", "Official travel document", "ticket", "money", "phone", "noun");
        createAndSaveBlank(travelPassage2, 2, "luggage", "수하물", "Bags and suitcases for travel", "food", "clothes", "books", "noun");
        createAndSaveBlank(travelPassage2, 3, "liquid", "액체", "Substance that flows freely", "solid", "metal", "paper", "noun");
        createAndSaveBlank(travelPassage2, 4, "boarding", "탑승", "Getting on a plane or ship", "landing", "parking", "stopping", "noun");
        createAndSaveBlank(travelPassage2, 5, "depart", "출발하다", "To leave or set off", "arrive", "stop", "wait", "verb");


        // Theme 3: Technology
        ClozeTheme techTheme = createTheme(
            "Technology & Innovation",
            "기술과 혁신",
            "Modern vocabulary related to technology, software, and digital innovation.",
            4,
            "Technology",
            3
        );
        techTheme = themeRepository.save(techTheme);

        // Technology - Passage 1
        ClozePassage techPassage1 = createPassage(
            techTheme,
            "Software Development",
            "Our team is {{1}} a new mobile application. We use {{2}} methodology for faster delivery. " +
            "The {{3}} phase will begin next week. We need to fix several {{4}} before the release. " +
            "The user {{5}} has been completely redesigned for better usability.",
            "우리 팀은 새로운 모바일 애플리케이션을 개발하고 있습니다. 더 빠른 배포를 위해 애자일 방법론을 사용합니다. " +
            "테스트 단계는 다음 주에 시작됩니다. 출시 전에 여러 버그를 수정해야 합니다. " +
            "더 나은 사용성을 위해 사용자 인터페이스가 완전히 재설계되었습니다.",
            1
        );
        techPassage1 = passageRepository.save(techPassage1);

        createAndSaveBlank(techPassage1, 1, "developing", "개발하는", "Creating or building software", "deleting", "copying", "selling", "verb");
        createAndSaveBlank(techPassage1, 2, "agile", "애자일", "Flexible project management approach", "waterfall", "slow", "rigid", "adjective");
        createAndSaveBlank(techPassage1, 3, "testing", "테스트", "Checking for errors or problems", "selling", "buying", "ignoring", "noun");
        createAndSaveBlank(techPassage1, 4, "bugs", "버그", "Errors or defects in software", "features", "updates", "users", "noun");
        createAndSaveBlank(techPassage1, 5, "interface", "인터페이스", "Point of interaction between user and system", "database", "server", "network", "noun");


        // Theme 4: Healthcare
        ClozeTheme healthTheme = createTheme(
            "Healthcare",
            "건강 관리",
            "Medical and health-related vocabulary for doctor visits and wellness discussions.",
            3,
            "Daily Life",
            4
        );
        healthTheme = themeRepository.save(healthTheme);

        // Healthcare - Passage 1
        ClozePassage healthPassage1 = createPassage(
            healthTheme,
            "Doctor's Appointment",
            "Good morning. What {{1}} bring you here today? I've been experiencing severe {{2}} for three days. " +
            "Let me check your {{3}}. Your blood pressure is slightly elevated. I'll {{4}} some medication. " +
            "Please take this {{5}} twice a day after meals.",
            "안녕하세요. 오늘 무슨 일로 오셨나요? 3일 동안 심한 두통이 있었습니다. " +
            "체온을 확인해 보겠습니다. 혈압이 약간 높습니다. 약을 처방해 드리겠습니다. " +
            "이 약을 식후에 하루 두 번 복용하세요.",
            1
        );
        healthPassage1 = passageRepository.save(healthPassage1);

        createAndSaveBlank(healthPassage1, 1, "symptoms", "증상", "Signs of illness or disease", "questions", "problems", "stories", "noun");
        createAndSaveBlank(healthPassage1, 2, "headaches", "두통", "Pain in the head", "happiness", "hunger", "energy", "noun");
        createAndSaveBlank(healthPassage1, 3, "temperature", "체온", "Body heat measurement", "weight", "height", "age", "noun");
        createAndSaveBlank(healthPassage1, 4, "prescribe", "처방하다", "To authorize medicine for treatment", "forbid", "ignore", "cancel", "verb");
        createAndSaveBlank(healthPassage1, 5, "medicine", "약", "Substance used to treat illness", "food", "water", "exercise", "noun");

        logger.info("Created 4 themes with {} passages total", passageRepository.count());
    }

    private ClozeTheme createTheme(String name, String nameKo, String description,
                                    Integer difficulty, String category, Integer order) {
        ClozeTheme theme = new ClozeTheme();
        theme.setThemeName(name);
        theme.setThemeNameKo(nameKo);
        theme.setDescription(description);
        theme.setDifficultyLevel(difficulty);
        theme.setCategory(category);
        theme.setDisplayOrder(order);
        theme.setActiveYn(YesNo.Y);
        theme.setCreateDt(LocalDateTime.now());
        theme.setUpdateDt(LocalDateTime.now());
        return theme;
    }

    private ClozePassage createPassage(ClozeTheme theme, String title, String content,
                                        String contentKo, Integer order) {
        ClozePassage passage = new ClozePassage();
        passage.setTheme(theme);
        passage.setTitle(title);
        passage.setContent(content);
        passage.setContentKo(contentKo);
        passage.setPassageOrder(order);
        passage.setActiveYn(YesNo.Y);
        passage.setCreateDt(LocalDateTime.now());
        passage.setUpdateDt(LocalDateTime.now());
        return passage;
    }

    private void createAndSaveBlank(ClozePassage passage, Integer blankNumber, String answer,
                                     String answerKo, String hint, String opt1, String opt2,
                                     String opt3, String wordClass) {
        ClozeBlank blank = new ClozeBlank();
        blank.setPassage(passage);
        blank.setBlankNumber(blankNumber);
        blank.setAnswer(answer);
        blank.setAnswerKo(answerKo);
        blank.setHint(hint);
        blank.setOption1(opt1);
        blank.setOption2(opt2);
        blank.setOption3(opt3);
        blank.setWordClass(wordClass);
        blank.setActiveYn(YesNo.Y);
        blank.setCreateDt(LocalDateTime.now());
        blank.setUpdateDt(LocalDateTime.now());
        blankRepository.save(blank);
    }
}
