-- V1: Create IRT/CAT tables
-- Compatible with MySQL (InnoDB). Adjust types for other DBs.

CREATE TABLE IF NOT EXISTS `word` (
  `word_seqno` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `word` VARCHAR(255) NOT NULL,
  `korean` VARCHAR(255),
  `detail_section` INT,
  `difficulty` DOUBLE DEFAULT NULL,
  `discrimination` DOUBLE DEFAULT 1.0,
  `guessing` DOUBLE DEFAULT 0.25,
  `response_count` INT DEFAULT 0,
  `correct_count` INT DEFAULT 0,
  `last_calibrated` DATETIME,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_detail_section` (`detail_section`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `word_exam` (
  `word_exam_seqno` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `initial_theta` DOUBLE DEFAULT 0.0,
  `final_theta` DOUBLE,
  `standard_error` DOUBLE,
  `question_count` INT DEFAULT 0,
  `exam_start_dt` DATETIME,
  `exam_end_dt` DATETIME,
  `exam_done_yn` CHAR(1) DEFAULT 'N',
  `exam_level` INT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_exam_start_dt` (`exam_start_dt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `word_exam_detail` (
  `detail_seqno` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `word_exam_seqno` BIGINT NOT NULL,
  `word_seqno` BIGINT NOT NULL,
  `exam_order` INT,
  `answer` VARCHAR(255),
  `correct_yn` BOOLEAN,
  `theta_before` DOUBLE,
  `theta_after` DOUBLE,
  `response_time_ms` INT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_word_exam` (`word_exam_seqno`),
  INDEX `idx_word` (`word_seqno`),
  CONSTRAINT `fk_wed_word_exam` FOREIGN KEY (`word_exam_seqno`) REFERENCES `word_exam`(`word_exam_seqno`) ON DELETE CASCADE,
  CONSTRAINT `fk_wed_word` FOREIGN KEY (`word_seqno`) REFERENCES `word`(`word_seqno`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `word_response_log` (
  `log_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `word_seqno` BIGINT NOT NULL,
  `theta_at_response` DOUBLE NOT NULL,
  `is_correct` BOOLEAN NOT NULL,
  `response_time_ms` INT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_word_seqno` (`word_seqno`),
  INDEX `idx_created_at` (`created_at`),
  CONSTRAINT `fk_wrl_word` FOREIGN KEY (`word_seqno`) REFERENCES `word`(`word_seqno`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `calibration_history` (
  `calibration_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `word_seqno` BIGINT NOT NULL,
  `old_difficulty` DOUBLE,
  `new_difficulty` DOUBLE,
  `old_discrimination` DOUBLE,
  `new_discrimination` DOUBLE,
  `sample_size` INT,
  `calibrated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_ch_word` FOREIGN KEY (`word_seqno`) REFERENCES `word`(`word_seqno`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- End of V1
