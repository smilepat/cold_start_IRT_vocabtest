import React from 'react';
import styles from './LearningMode.module.css';

const LearningResult = ({ answers, level, onRetry, onBack, onTypeSelect }) => {
  const totalCount = answers.length;
  const correctCount = answers.filter((a) => a.correct).length;
  const accuracy = totalCount > 0 ? Math.round((correctCount / totalCount) * 100) : 0;

  // Group by question type
  const typeStats = {};
  answers.forEach((a) => {
    const key = a.questionTypeLabel || a.questionType;
    if (!typeStats[key]) typeStats[key] = { correct: 0, total: 0 };
    typeStats[key].total++;
    if (a.correct) typeStats[key].correct++;
  });

  // Wrong answers list
  const wrongAnswers = answers.filter((a) => !a.correct);

  const getGrade = () => {
    if (accuracy >= 90) return { text: '훌륭해요! 🎉', color: '#4caf50' };
    if (accuracy >= 70) return { text: '잘했어요! 👍', color: '#2196f3' };
    if (accuracy >= 50) return { text: '더 노력해봐요! 💪', color: '#ff9800' };
    return { text: '복습이 필요해요 📖', color: '#f44336' };
  };

  const grade = getGrade();

  return (
    <div className={styles.resultPanel}>
      <h2 className={styles.resultTitle}>학습 결과</h2>

      {/* Score circle */}
      <div className={styles.scoreCircle} style={{ borderColor: grade.color }}>
        <div className={styles.scorePercent} style={{ color: grade.color }}>
          {accuracy}%
        </div>
        <div className={styles.scoreDetail}>
          {correctCount} / {totalCount}
        </div>
      </div>
      <p className={styles.gradeText} style={{ color: grade.color }}>
        {grade.text}
      </p>

      {/* Type breakdown */}
      <div className={styles.typeBreakdown}>
        <h3>유형별 성적</h3>
        {Object.entries(typeStats).map(([type, stat]) => (
          <div key={type} className={styles.typeRow}>
            <span className={styles.typeName}>{type}</span>
            <span className={styles.typeStat}>
              {stat.correct}/{stat.total} ({Math.round((stat.correct / stat.total) * 100)}%)
            </span>
          </div>
        ))}
      </div>

      {/* Wrong answers */}
      {wrongAnswers.length > 0 && (
        <div className={styles.wrongSection}>
          <h3>틀린 단어 복습</h3>
          {wrongAnswers.map((a, idx) => (
            <div key={idx} className={styles.wrongItem}>
              <strong>{a.word}</strong>
              <span className={styles.wrongMeaning}>{a.koreanDef}</span>
              {a.example && (
                <span className={styles.wrongExample}>{a.example}</span>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Action buttons */}
      <div className={styles.resultActions}>
        <button className={styles.retryBtn} onClick={onRetry}>
          다시 풀기
        </button>
        <button className={styles.typeSelectBtn} onClick={onTypeSelect}>
          유형 변경
        </button>
        <button className={styles.backBtn} onClick={onBack}>
          결과로 돌아가기
        </button>
      </div>
    </div>
  );
};

export default LearningResult;
