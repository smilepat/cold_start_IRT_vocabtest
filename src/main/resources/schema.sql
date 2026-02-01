-- =============================================================================
-- IRT CAT Vocabulary Test - Database Schema (H2 Compatible)
-- Cold Start IRT-based Computerized Adaptive Testing
-- =============================================================================

-- Table: word (Vocabulary Items with IRT Parameters)
CREATE TABLE IF NOT EXISTS word (
    word_seqno BIGINT AUTO_INCREMENT PRIMARY KEY,
    level INT DEFAULT 0,
    detail_section INT DEFAULT 0,
    word VARCHAR(255),
    meaning TEXT,
    example_sentence TEXT,
    korean VARCHAR(255),
    option1 VARCHAR(255),
    option2 VARCHAR(255),
    option3 VARCHAR(255),
    unknown_option VARCHAR(255) DEFAULT '모르겠습니다',
    answer VARCHAR(255),
    active_yn VARCHAR(1) DEFAULT 'Y',
    -- IRT Parameters (3PL Model)
    difficulty DOUBLE DEFAULT NULL,
    discrimination DOUBLE DEFAULT 1.0,
    guessing DOUBLE DEFAULT 0.25,
    -- Calibration Statistics
    response_count INT DEFAULT 0,
    correct_count INT DEFAULT 0,
    last_calibrated TIMESTAMP DEFAULT NULL,
    -- Timestamps
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: word_exam (CAT Test Sessions)
CREATE TABLE IF NOT EXISTS word_exam (
    word_exam_seqno BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_start_dt TIMESTAMP,
    exam_end_dt TIMESTAMP,
    exam_done_yn VARCHAR(1) DEFAULT 'N',
    score INT DEFAULT 0,
    exam_level INT,
    exam_detail_section INT,
    -- IRT Theta Estimation
    initial_theta DOUBLE DEFAULT 0.0,
    final_theta DOUBLE DEFAULT NULL,
    standard_error DOUBLE DEFAULT NULL,
    question_count INT DEFAULT 0,
    termination_reason VARCHAR(50) DEFAULT NULL
);

-- Table: word_exam_detail (Individual Item Responses)
CREATE TABLE IF NOT EXISTS word_exam_detail (
    word_exam_detail_seqno BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_exam_seqno BIGINT NOT NULL,
    exam_order INT,
    word_seqno BIGINT,
    word_seqno_low_limit BIGINT DEFAULT NULL,
    word_seqno_high_limit BIGINT DEFAULT NULL,
    answer VARCHAR(255),
    correct_yn VARCHAR(1),
    -- IRT Tracking
    theta_before DOUBLE DEFAULT NULL,
    theta_after DOUBLE DEFAULT NULL,
    se_before DOUBLE DEFAULT NULL,
    se_after DOUBLE DEFAULT NULL,
    item_information DOUBLE DEFAULT NULL,
    response_time_ms INT DEFAULT NULL,
    -- Timestamps
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_exam_seqno) REFERENCES word_exam(word_exam_seqno),
    FOREIGN KEY (word_seqno) REFERENCES word(word_seqno)
);

-- Table: word_response_log (For Calibration)
CREATE TABLE IF NOT EXISTS word_response_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_seqno BIGINT NOT NULL,
    theta_at_response DOUBLE NOT NULL,
    is_correct BOOLEAN NOT NULL,
    response_time_ms INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_seqno) REFERENCES word(word_seqno) ON DELETE CASCADE
);

-- Table: calibration_history (Parameter Change History)
CREATE TABLE IF NOT EXISTS calibration_history (
    calibration_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_seqno BIGINT NOT NULL,
    old_difficulty DOUBLE DEFAULT NULL,
    new_difficulty DOUBLE DEFAULT NULL,
    old_discrimination DOUBLE DEFAULT NULL,
    new_discrimination DOUBLE DEFAULT NULL,
    sample_size INT DEFAULT NULL,
    calibrated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_seqno) REFERENCES word(word_seqno) ON DELETE CASCADE
);

-- =============================================================================
-- Cloze Learning Tables
-- =============================================================================

-- Table: cloze_theme (주제별 학습 카테고리)
CREATE TABLE IF NOT EXISTS cloze_theme (
    theme_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_name VARCHAR(100) NOT NULL,
    theme_name_ko VARCHAR(100),
    description VARCHAR(500),
    difficulty_level INT DEFAULT 1,
    category VARCHAR(50),
    thumbnail_url VARCHAR(255),
    display_order INT DEFAULT 0,
    active_yn VARCHAR(1) DEFAULT 'Y',
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: cloze_passage (지문)
CREATE TABLE IF NOT EXISTS cloze_passage (
    passage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    theme_id BIGINT NOT NULL,
    title VARCHAR(200),
    content TEXT,
    content_ko TEXT,
    passage_order INT DEFAULT 0,
    active_yn VARCHAR(1) DEFAULT 'Y',
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (theme_id) REFERENCES cloze_theme(theme_id) ON DELETE CASCADE
);

-- Table: cloze_blank (빈칸)
CREATE TABLE IF NOT EXISTS cloze_blank (
    blank_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    passage_id BIGINT NOT NULL,
    blank_number INT,
    answer VARCHAR(100) NOT NULL,
    answer_ko VARCHAR(100),
    hint VARCHAR(200),
    option1 VARCHAR(100),
    option2 VARCHAR(100),
    option3 VARCHAR(100),
    word_class VARCHAR(50),
    active_yn VARCHAR(1) DEFAULT 'Y',
    create_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (passage_id) REFERENCES cloze_passage(passage_id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_word_level ON word(level, detail_section);
CREATE INDEX IF NOT EXISTS idx_word_active ON word(active_yn);
CREATE INDEX IF NOT EXISTS idx_word_difficulty ON word(difficulty);
CREATE INDEX IF NOT EXISTS idx_exam_done ON word_exam(exam_done_yn);
CREATE INDEX IF NOT EXISTS idx_exam_detail_exam ON word_exam_detail(word_exam_seqno);
CREATE INDEX IF NOT EXISTS idx_wrl_word_seqno ON word_response_log(word_seqno);
CREATE INDEX IF NOT EXISTS idx_ch_word_seqno ON calibration_history(word_seqno);
CREATE INDEX IF NOT EXISTS idx_cloze_theme_active ON cloze_theme(active_yn);
CREATE INDEX IF NOT EXISTS idx_cloze_theme_category ON cloze_theme(category);
CREATE INDEX IF NOT EXISTS idx_cloze_passage_theme ON cloze_passage(theme_id);
CREATE INDEX IF NOT EXISTS idx_cloze_blank_passage ON cloze_blank(passage_id);
