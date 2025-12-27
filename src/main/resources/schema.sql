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

-- Indexes
CREATE INDEX IF NOT EXISTS idx_word_level ON word(level, detail_section);
CREATE INDEX IF NOT EXISTS idx_word_active ON word(active_yn);
CREATE INDEX IF NOT EXISTS idx_word_difficulty ON word(difficulty);
CREATE INDEX IF NOT EXISTS idx_exam_done ON word_exam(exam_done_yn);
CREATE INDEX IF NOT EXISTS idx_exam_detail_exam ON word_exam_detail(word_exam_seqno);
CREATE INDEX IF NOT EXISTS idx_wrl_word_seqno ON word_response_log(word_seqno);
CREATE INDEX IF NOT EXISTS idx_ch_word_seqno ON calibration_history(word_seqno);
