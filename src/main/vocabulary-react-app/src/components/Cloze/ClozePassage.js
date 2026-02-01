import React, { useState, useEffect } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import Chip from '@material-ui/core/Chip';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import { makeStyles } from '@material-ui/core/styles';
import axios from '../../axios/axios';

const useStyles = makeStyles((theme) => ({
  container: {
    padding: '40px',
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  },
  header: {
    textAlign: 'center',
    marginBottom: '30px',
    color: '#fff',
  },
  title: {
    fontSize: '3rem',
    fontWeight: 'bold',
    marginBottom: '10px',
    fontFamily: 'JalnanOTF',
  },
  passageCard: {
    borderRadius: '16px',
    marginBottom: '20px',
    overflow: 'hidden',
  },
  passageTitle: {
    fontSize: '2rem',
    fontWeight: 'bold',
    marginBottom: '16px',
    color: '#667eea',
  },
  passageContent: {
    fontSize: '1.8rem',
    lineHeight: 2,
    color: '#333',
    marginBottom: '20px',
  },
  blankInput: {
    display: 'inline-block',
    minWidth: '120px',
    margin: '0 4px',
    verticalAlign: 'middle',
  },
  blankInputField: {
    '& input': {
      fontSize: '1.6rem',
      textAlign: 'center',
      padding: '8px',
    },
  },
  correctInput: {
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: '#4caf50',
        borderWidth: '2px',
      },
    },
    '& input': {
      color: '#4caf50',
      fontWeight: 'bold',
    },
  },
  incorrectInput: {
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: '#f44336',
        borderWidth: '2px',
      },
    },
    '& input': {
      color: '#f44336',
    },
  },
  translationSection: {
    marginTop: '20px',
    padding: '16px',
    background: '#f5f5f5',
    borderRadius: '8px',
  },
  translationTitle: {
    fontSize: '1.4rem',
    fontWeight: 'bold',
    color: '#666',
    marginBottom: '8px',
  },
  translationText: {
    fontSize: '1.6rem',
    color: '#333',
    lineHeight: 1.8,
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'center',
    gap: '16px',
    marginTop: '20px',
  },
  checkButton: {
    fontSize: '1.6rem',
    padding: '12px 32px',
    borderRadius: '8px',
    background: '#667eea',
    color: '#fff',
    '&:hover': {
      background: '#5a6fd6',
    },
  },
  hintButton: {
    fontSize: '1.6rem',
    padding: '12px 32px',
    borderRadius: '8px',
  },
  nextButton: {
    fontSize: '1.6rem',
    padding: '12px 32px',
    borderRadius: '8px',
    background: '#4caf50',
    color: '#fff',
    '&:hover': {
      background: '#43a047',
    },
  },
  backButton: {
    position: 'absolute',
    top: '20px',
    left: '20px',
    background: 'rgba(255,255,255,0.2)',
    color: '#fff',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '1.4rem',
    '&:hover': {
      background: 'rgba(255,255,255,0.3)',
    },
  },
  progressInfo: {
    textAlign: 'center',
    color: '#fff',
    marginBottom: '20px',
    fontSize: '1.6rem',
  },
  optionsContainer: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '8px',
    marginTop: '16px',
    justifyContent: 'center',
  },
  optionChip: {
    fontSize: '1.4rem',
    padding: '8px 16px',
    cursor: 'pointer',
    '&:hover': {
      transform: 'scale(1.05)',
    },
  },
  scoreDisplay: {
    textAlign: 'center',
    color: '#fff',
    fontSize: '2rem',
    marginTop: '20px',
  },
  resultDialog: {
    '& .MuiDialog-paper': {
      borderRadius: '16px',
      padding: '20px',
    },
  },
}));

function ClozePassage() {
  const classes = useStyles();
  const { themeId } = useParams();
  const history = useHistory();

  const [theme, setTheme] = useState(null);
  const [passages, setPassages] = useState([]);
  const [currentPassageIndex, setCurrentPassageIndex] = useState(0);
  const [userAnswers, setUserAnswers] = useState({});
  const [results, setResults] = useState({});
  const [showTranslation, setShowTranslation] = useState(false);
  const [score, setScore] = useState(0);
  const [totalBlanks, setTotalBlanks] = useState(0);
  const [showResult, setShowResult] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchThemeAndPassages();
  }, [themeId]);

  const fetchThemeAndPassages = async () => {
    try {
      setLoading(true);
      const [themeRes, passagesRes] = await Promise.all([
        axios.get(`/api/cloze/themes/${themeId}`),
        axios.get(`/api/cloze/themes/${themeId}/passages/full`),
      ]);

      setTheme(themeRes.data.data);
      setPassages(passagesRes.data.data || []);

      // Calculate total blanks
      const blanks = (passagesRes.data.data || []).reduce(
        (acc, p) => acc + (p.blanks?.length || 0),
        0
      );
      setTotalBlanks(blanks);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  };

  const currentPassage = passages[currentPassageIndex];

  const handleAnswerChange = (blankId, value) => {
    setUserAnswers((prev) => ({
      ...prev,
      [blankId]: value,
    }));
  };

  const handleOptionSelect = (blankId, option) => {
    handleAnswerChange(blankId, option);
  };

  const checkAnswers = async () => {
    if (!currentPassage?.blanks) return;

    const newResults = { ...results };
    let correctCount = 0;

    for (const blank of currentPassage.blanks) {
      const userAnswer = userAnswers[blank.blankId] || '';
      const isCorrect = userAnswer.toLowerCase().trim() === blank.answer.toLowerCase();
      newResults[blank.blankId] = {
        correct: isCorrect,
        correctAnswer: blank.answer,
        answerKo: blank.answerKo,
      };
      if (isCorrect) correctCount++;
    }

    setResults(newResults);
    setScore((prev) => prev + correctCount);
    setShowTranslation(true);
  };

  const handleNext = () => {
    if (currentPassageIndex < passages.length - 1) {
      setCurrentPassageIndex((prev) => prev + 1);
      setShowTranslation(false);
    } else {
      setShowResult(true);
    }
  };

  const handleBack = () => {
    history.push('/cloze');
  };

  const handleFinish = () => {
    history.push('/cloze');
  };

  const renderPassageContent = () => {
    if (!currentPassage) return null;

    let content = currentPassage.content;
    const blanks = currentPassage.blanks || [];

    // Sort blanks by blank number
    const sortedBlanks = [...blanks].sort((a, b) => a.blankNumber - b.blankNumber);

    // Replace {{n}} with input fields
    const parts = [];
    let lastIndex = 0;

    sortedBlanks.forEach((blank) => {
      const placeholder = `{{${blank.blankNumber}}}`;
      const index = content.indexOf(placeholder, lastIndex);

      if (index !== -1) {
        // Add text before the blank
        if (index > lastIndex) {
          parts.push(
            <span key={`text-${blank.blankNumber}`}>
              {content.substring(lastIndex, index)}
            </span>
          );
        }

        // Add the input field
        const result = results[blank.blankId];
        let inputClass = classes.blankInputField;
        if (result) {
          inputClass += ' ' + (result.correct ? classes.correctInput : classes.incorrectInput);
        }

        parts.push(
          <span key={`blank-${blank.blankNumber}`} className={classes.blankInput}>
            <TextField
              variant="outlined"
              size="small"
              value={userAnswers[blank.blankId] || ''}
              onChange={(e) => handleAnswerChange(blank.blankId, e.target.value)}
              className={inputClass}
              placeholder={`(${blank.blankNumber})`}
              disabled={!!result}
            />
            {result && !result.correct && (
              <Typography
                component="span"
                style={{ color: '#4caf50', marginLeft: '8px', fontSize: '1.4rem' }}
              >
                → {result.correctAnswer}
              </Typography>
            )}
          </span>
        );

        lastIndex = index + placeholder.length;
      }
    });

    // Add remaining text
    if (lastIndex < content.length) {
      parts.push(<span key="text-end">{content.substring(lastIndex)}</span>);
    }

    return parts;
  };

  const renderOptions = () => {
    if (!currentPassage?.blanks || showTranslation) return null;

    // Collect all options for current passage
    const allOptions = [];
    currentPassage.blanks.forEach((blank) => {
      allOptions.push(blank.answer);
      if (blank.option1) allOptions.push(blank.option1);
      if (blank.option2) allOptions.push(blank.option2);
      if (blank.option3) allOptions.push(blank.option3);
    });

    // Shuffle options
    const shuffledOptions = [...new Set(allOptions)].sort(() => Math.random() - 0.5);

    return (
      <div className={classes.optionsContainer}>
        {shuffledOptions.map((option, idx) => (
          <Chip
            key={idx}
            label={option}
            className={classes.optionChip}
            onClick={() => {
              // Find the first empty blank and fill it
              const emptyBlank = currentPassage.blanks.find(
                (b) => !userAnswers[b.blankId] && !results[b.blankId]
              );
              if (emptyBlank) {
                handleOptionSelect(emptyBlank.blankId, option);
              }
            }}
            variant="outlined"
          />
        ))}
      </div>
    );
  };

  if (loading) {
    return (
      <div className={classes.container}>
        <Typography style={{ textAlign: 'center', color: '#fff' }}>Loading...</Typography>
      </div>
    );
  }

  return (
    <div className={classes.container}>
      <button className={classes.backButton} onClick={handleBack}>
        ← Back to Themes
      </button>

      <div className={classes.header}>
        <Typography className={classes.title}>
          {theme?.themeName || 'Cloze Learning'}
        </Typography>
        <Typography style={{ fontSize: '1.6rem', opacity: 0.9 }}>
          {theme?.themeNameKo}
        </Typography>
      </div>

      <Typography className={classes.progressInfo}>
        Passage {currentPassageIndex + 1} of {passages.length}
      </Typography>

      {currentPassage && (
        <Card className={classes.passageCard}>
          <CardContent style={{ padding: '32px' }}>
            <Typography className={classes.passageTitle}>
              {currentPassage.title}
            </Typography>

            <Typography component="div" className={classes.passageContent}>
              {renderPassageContent()}
            </Typography>

            {renderOptions()}

            {showTranslation && (
              <div className={classes.translationSection}>
                <Typography className={classes.translationTitle}>
                  한글 해석
                </Typography>
                <Typography className={classes.translationText}>
                  {currentPassage.contentKo}
                </Typography>
              </div>
            )}

            <div className={classes.buttonContainer}>
              {!showTranslation ? (
                <Button
                  variant="contained"
                  className={classes.checkButton}
                  onClick={checkAnswers}
                >
                  Check Answers
                </Button>
              ) : (
                <Button
                  variant="contained"
                  className={classes.nextButton}
                  onClick={handleNext}
                >
                  {currentPassageIndex < passages.length - 1 ? 'Next Passage' : 'See Results'}
                </Button>
              )}
            </div>
          </CardContent>
        </Card>
      )}

      <Typography className={classes.scoreDisplay}>
        Score: {score} / {totalBlanks}
      </Typography>

      <Dialog open={showResult} className={classes.resultDialog}>
        <DialogTitle style={{ fontSize: '2.4rem', textAlign: 'center' }}>
          Congratulations!
        </DialogTitle>
        <DialogContent>
          <Typography style={{ fontSize: '1.8rem', textAlign: 'center', marginBottom: '16px' }}>
            You completed the {theme?.themeName} theme!
          </Typography>
          <Typography style={{ fontSize: '2.4rem', textAlign: 'center', color: '#667eea', fontWeight: 'bold' }}>
            Final Score: {score} / {totalBlanks}
          </Typography>
          <Typography style={{ fontSize: '1.6rem', textAlign: 'center', marginTop: '16px', color: '#666' }}>
            Accuracy: {totalBlanks > 0 ? Math.round((score / totalBlanks) * 100) : 0}%
          </Typography>
        </DialogContent>
        <DialogActions style={{ justifyContent: 'center', padding: '20px' }}>
          <Button
            variant="contained"
            onClick={handleFinish}
            style={{ fontSize: '1.6rem', padding: '12px 32px', background: '#667eea', color: '#fff' }}
          >
            Back to Themes
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default ClozePassage;
