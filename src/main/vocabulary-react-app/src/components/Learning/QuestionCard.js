import React, { useState } from 'react';
import styles from './LearningMode.module.css';

const QuestionCard = ({ question, onAnswer, answerResult }) => {
  const [selected, setSelected] = useState(null);
  const [submitted, setSubmitted] = useState(false);

  // Reset when question changes
  React.useEffect(() => {
    setSelected(null);
    setSubmitted(false);
  }, [question.word, question.questionType]);

  const handleSelect = (label) => {
    if (submitted) return;
    setSelected(label);
  };

  const handleSubmit = () => {
    if (!selected || submitted) return;
    setSubmitted(true);
    onAnswer(selected);
  };

  // Format question text (preserve line breaks)
  const formatQuestion = (text) => {
    if (!text) return '';
    return text.split('\n').map((line, i) => (
      <span key={i}>
        {line}
        {i < text.split('\n').length - 1 && <br />}
      </span>
    ));
  };

  const isCorrect = answerResult && answerResult.correct;
  const correctLabel = answerResult ? answerResult.correctAnswer : null;

  return (
    <div className={styles.questionCard}>
      {/* Word badge */}
      <div className={styles.wordBadge}>
        <span className={styles.wordText}>{question.word}</span>
        <span className={styles.posBadge}>{question.pos}</span>
      </div>

      {/* Question text */}
      <div className={styles.questionText}>
        {formatQuestion(question.question)}
      </div>

      {/* Choices */}
      <div className={styles.choicesGrid}>
        {question.choices && question.choices.map((choice, idx) => {
          const label = question.choiceLabels ? question.choiceLabels[idx] : String.fromCharCode(65 + idx);
          const isSelected = selected === label;
          const isCorrectChoice = submitted && correctLabel === label;
          const isWrongChoice = submitted && isSelected && !isCorrect && selected === label;

          let className = styles.choiceBtn;
          if (isSelected && !submitted) className += ` ${styles.choiceBtnSelected}`;
          if (isCorrectChoice) className += ` ${styles.choiceBtnCorrect}`;
          if (isWrongChoice) className += ` ${styles.choiceBtnWrong}`;

          return (
            <button
              key={idx}
              className={className}
              onClick={() => handleSelect(label)}
              disabled={submitted}
            >
              <span className={styles.choiceLabel}>{label}</span>
              <span className={styles.choiceText}>{choice}</span>
            </button>
          );
        })}
      </div>

      {/* Submit / Feedback */}
      {!submitted ? (
        <button
          className={`${styles.submitBtn} ${selected ? styles.submitBtnActive : ''}`}
          onClick={handleSubmit}
          disabled={!selected}
        >
          정답 확인
        </button>
      ) : (
        <div className={`${styles.feedback} ${isCorrect ? styles.feedbackCorrect : styles.feedbackWrong}`}>
          <div className={styles.feedbackIcon}>
            {isCorrect ? '✓ 정답!' : '✗ 오답'}
          </div>
          {!isCorrect && (
            <div className={styles.feedbackDetail}>
              정답: {correctLabel}
            </div>
          )}
          <div className={styles.feedbackMeaning}>
            <strong>{question.word}</strong>: {question.koreanDef}
          </div>
        </div>
      )}
    </div>
  );
};

export default QuestionCard;
