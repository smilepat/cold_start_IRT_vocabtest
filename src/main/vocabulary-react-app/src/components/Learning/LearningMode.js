import React, { useState, useEffect } from 'react';
import { withRouter } from 'react-router-dom';
import axios from '../../axios/axios';
import QuestionCard from './QuestionCard';
import LearningResult from './LearningResult';
import Steps from 'components/Steps/Steps';
import styles from './LearningMode.module.css';

const LearningMode = ({ history, examId }) => {
  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [finished, setFinished] = useState(false);
  const [availableTypes, setAvailableTypes] = useState([]);
  const [selectedType, setSelectedType] = useState('');
  const [setupDone, setSetupDone] = useState(false);

  // Parse query params
  const params = new URLSearchParams(window.location.hash.split('?')[1] || '');
  const level = parseInt(params.get('level')) || 1;
  const count = parseInt(params.get('count')) || 10;

  // Load available question types for this level
  useEffect(() => {
    async function loadTypes() {
      try {
        const res = await axios.get(`/api/learning/types?level=${level}`);
        if (res.data.success) {
          setAvailableTypes(res.data.data);
        }
      } catch (e) {
        console.error('Failed to load types:', e);
      }
    }
    loadTypes();
  }, [level]);

  // Load questions when type is selected or setup is done
  const startLearning = async (type) => {
    setLoading(true);
    setError(null);
    try {
      const typeParam = type ? `&type=${type}` : '';
      const res = await axios.get(
        `/api/learning/questions?level=${level}&count=${count}${typeParam}`
      );
      if (res.data.success && res.data.data.length > 0) {
        setQuestions(res.data.data);
        setAnswers([]);
        setCurrentIndex(0);
        setFinished(false);
        setSetupDone(true);
      } else {
        setError('해당 레벨에 학습 문제가 없습니다.');
      }
    } catch (e) {
      setError('문제를 불러오는 데 실패했습니다.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  // Handle answer submission
  const handleAnswer = async (userAnswer) => {
    const q = questions[currentIndex];
    try {
      const res = await axios.post(
        `/api/learning/check?word=${encodeURIComponent(q.word)}&questionType=${q.questionType}&userAnswer=${userAnswer}`
      );
      const result = res.data.data;
      const newAnswer = {
        ...result,
        userAnswer,
        questionType: q.questionType,
        questionTypeLabel: q.questionTypeLabel,
        word: q.word,
      };
      setAnswers((prev) => [...prev, newAnswer]);

      // Auto advance after 1.5s
      setTimeout(() => {
        if (currentIndex + 1 < questions.length) {
          setCurrentIndex((prev) => prev + 1);
        } else {
          setFinished(true);
        }
      }, 1500);
    } catch (e) {
      console.error('Answer check failed:', e);
    }
  };

  // Retry with same settings
  const handleRetry = () => {
    startLearning(selectedType);
  };

  // Type selection screen
  if (!setupDone) {
    return (
      <div className={styles.container}>
        <Steps />
        <div className={styles.setupPanel}>
          <h2 className={styles.setupTitle}>어휘 학습 모드</h2>
          <p className={styles.setupDesc}>
            Level {level} 학습을 시작합니다. 문제 유형을 선택하세요.
          </p>

          <div className={styles.typeGrid}>
            <button
              className={`${styles.typeBtn} ${styles.typeBtnAll}`}
              onClick={() => {
                setSelectedType('');
                startLearning('');
              }}
            >
              🎲 전체 유형 랜덤
            </button>
            {availableTypes.map((t) => (
              <button
                key={t.type}
                className={styles.typeBtn}
                onClick={() => {
                  setSelectedType(t.type);
                  startLearning(t.type);
                }}
              >
                {t.label}
              </button>
            ))}
          </div>

          <button
            className={styles.backBtn}
            onClick={() => history.push('/result')}
          >
            ← 결과로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  // Loading
  if (loading) {
    return (
      <div className={styles.container}>
        <Steps />
        <div className={styles.loadingPanel}>
          <div className={styles.spinner}></div>
          <p>문제를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  // Error
  if (error) {
    return (
      <div className={styles.container}>
        <Steps />
        <div className={styles.errorPanel}>
          <p>{error}</p>
          <button onClick={() => setSetupDone(false)}>다시 선택</button>
        </div>
      </div>
    );
  }

  // Finished - show result
  if (finished) {
    return (
      <div className={styles.container}>
        <Steps />
        <LearningResult
          answers={answers}
          level={level}
          onRetry={handleRetry}
          onBack={() => history.push('/result')}
          onTypeSelect={() => setSetupDone(false)}
        />
      </div>
    );
  }

  // Question display
  const currentQuestion = questions[currentIndex];
  const currentAnswer = answers.length > currentIndex ? answers[currentIndex] : null;

  return (
    <div className={styles.container}>
      <Steps />
      <div className={styles.mainPanel}>
        {/* Progress bar */}
        <div className={styles.progressSection}>
          <div className={styles.progressInfo}>
            <span className={styles.progressLabel}>
              Level {level} · {currentQuestion.questionTypeLabel}
            </span>
            <span className={styles.progressCount}>
              {currentIndex + 1} / {questions.length}
            </span>
          </div>
          <div className={styles.progressBar}>
            <div
              className={styles.progressFill}
              style={{ width: `${((currentIndex + 1) / questions.length) * 100}%` }}
            />
          </div>
        </div>

        {/* Question card */}
        <QuestionCard
          question={currentQuestion}
          onAnswer={handleAnswer}
          answerResult={currentAnswer}
        />

        {/* Score so far */}
        <div className={styles.scoreSection}>
          <span>
            정답: {answers.filter((a) => a.correct).length} / {answers.length}
          </span>
        </div>
      </div>
    </div>
  );
};

export default withRouter(LearningMode);
