import React, {useState, useEffect, useRef} from 'react';
import axios from "../../axios/axios"
import {withRouter} from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import styles from './Main.module.css';
import Steps from 'components/Steps/Steps';
import FinishModal from '../FinishModal/FinishModal';
import InputModal from '../FinishModal/InputModal';
import ExitModal from 'components/QuitModal/QuitModal';
import LineChart from 'components/LineChart/LineChart';
import ProgressContainer from 'components/ProgressNew/ProgressNew';

import quitBtn from 'Images/Main/quit_btn1.png';
import nextBtnClicked from 'Images/Main/next_btn.png';
import nextBtnHover from 'Images/Main/skip-btn-1-h.png';
import nextBtnNormal from 'Images/Main/skip-btn-1-n.png';

const Main = ({
	history,
	examId,
	seqNo,
	open1,
	input1,
	open,
	onClickQuit,
	onClickFinish,
	onClickInput,
	handleClose,
	handleClose1,
	handleInput,
}) => {
	const [activeStep, setActiveStep] = useState(0);
	const [counter, setCounter] = useState(1);
	const [done, setDone] = useState(false);
	const [examResult, setExamResult] = useState([]);
	const [exampleSentence, setExampleSentence] = useState('');
	const [inputAnswer, setInputAnswer] = useState('');
	const [question, setQuestion] = useState('');
	const [level, setLevel] = useState(0);
	const [answerOption1, setAnswerOption1] = useState('');
	const [answerOption2, setAnswerOption2] = useState('');
	const [answerOption3, setAnswerOption3] = useState('');
	const [answerOption4, setAnswerOption4] = useState('');
	const [answerOption5, setAnswerOption5] = useState('');
	const [answerOptionId, setAnswerOptionId] = useState('');
	const [skipped, setSkipped] = useState(new Set());
	const [toggleVisibility, setToggleVisibility] = useState(false);
	const [nextBtn, setNextbtn] = useState('default');
	const [input, setInput] = useState(false);
	const [currentTheta, setCurrentTheta] = useState(0);
	const [standardError, setStandardError] = useState(1);
	const [responseStartTime, setResponseStartTime] = useState(Date.now());

	const isStepSkipped = (step) => skipped.has(step);
	const inputRef = useRef();

	useEffect(() => {
		if (input === true) {
			onClickInput();
			setInput(false);
		}
	}, [input, onClickInput]);

	useEffect(() => {
		if (done === true) {
			onClickFinish();
		}
	}, [done, onClickFinish]);

	// Fetch current question from IRT CAT API
	useEffect(() => {
		const fetchCurrentQuestion = async () => {
			try {
				const id = examId || seqNo;
				if (done === true || id === 0) return;

				const response = await axios.get(`/api/irt/exam/${id}/current`);
				const detail = response.data;

				if (detail && detail.word) {
					const word = detail.word;
					const opts = [word.korean, word.option1, word.option2, word.option3];
					opts.sort(() => 0.5 - Math.random());

					setAnswerOption1(opts[0] || '');
					setAnswerOption2(opts[1] || '');
					setAnswerOption3(opts[2] || '');
					setAnswerOption4(opts[3] || '');
					setAnswerOption5(word.unknown || '모르겠습니다');

					setLevel(word.level);
					setExampleSentence(word.exampleSentence || '');
					setQuestion(word.word);
					setAnswerOptionId('');
					setInputAnswer('');
					setResponseStartTime(Date.now());

					if (inputRef.current) {
						inputRef.current.focus();
					}
				}
			} catch (error) {
				console.error('Failed to fetch question:', error);
			}
		};
		fetchCurrentQuestion();
	}, [done, examId, seqNo, counter]);

	// Fetch exam history for chart
	useEffect(() => {
		const fetchExamResult = async () => {
			const id = examId || seqNo;
			if (counter <= 1 || id === 0) return;
			try {
				const res = await axios.get(`/api/irt/exam/${id}/result`);
				if (res.data && res.data.wordExamDetails) {
					setExamResult(res.data.wordExamDetails);
				}
			} catch (err) {
				console.error('Failed to fetch exam result:', err);
			}
		};
		fetchExamResult();
	}, [counter, examId, seqNo]);

	const nextQuestion = async (e) => {
		e.preventDefault();

		let newSkipped = skipped;
		if (isStepSkipped(activeStep)) {
			newSkipped = new Set(newSkipped.values());
			newSkipped.delete(activeStep);
		}
		setActiveStep((prev) => prev + 1);
		setSkipped(newSkipped);

		try {
			if (inputAnswer === '') {
				setInput(true);
				return;
			}

			if (done === false) {
				const id = examId || seqNo;
				const responseTimeMs = Date.now() - responseStartTime;

				// Submit answer to IRT CAT API
				const result = await axios.post(
					`/api/irt/exam/${id}/submit`,
					null,
					{
						params: {
							answer: inputAnswer,
							responseTimeMs: responseTimeMs
						}
					}
				);

				const submitResult = result.data;
				setCurrentTheta(submitResult.currentTheta || submitResult.finalTheta || 0);
				setStandardError(submitResult.standardError || 1);

				const isExamEnd = submitResult.examEnd;
				setInputAnswer('');
				setDone(isExamEnd);
				setNextbtn('clicked');

				if (!isExamEnd) {
					setCounter(counter + 1);
				}
			}
		} catch (err) {
			console.error('Failed to submit answer:', err);
		}
	};

	useEffect(() => {
		setNextbtn('default');
	}, [counter]);

	const answerHandler = (e) => {
		setAnswerOptionId(e.target.id);
		setInputAnswer(e.target.value);
		setToggleVisibility(true);
	};

	const getSteps = Array.from({length: 30}, (_, i) => i + 1);

	return (
		<Grid container className={styles.mainWrapper}>
			<Grid className={styles.navWrapper}>
				<Grid className={styles.navText}>IRT CAT VOCABULARY TEST</Grid>
				<Grid className={styles.modalWrapper}>
					<img
						src={quitBtn}
						alt="quit"
						width='150'
						height='60'
						className={styles.quitBtn}
						onClick={onClickQuit}
					/>
				</Grid>
				<ExitModal
					open={open}
					onClickQuit={onClickQuit}
					handleClose={handleClose}
				/>
			</Grid>
			<Grid className={styles.bodyWrapper}>
				<Steps />

				<form className={styles.mainBodyContent} onSubmit={nextQuestion}>
					<ProgressContainer
						done={done}
						counter={counter}
						getSteps={getSteps}
						activeStep={activeStep}
						isStepSkipped={isStepSkipped}
					/>

					<Grid style={{textAlign: 'center', marginBottom: '10px', color: '#666'}}>
						θ: {currentTheta.toFixed(2)} | SE: {standardError.toFixed(2)} | 문항: {counter}
					</Grid>

					<Grid className={styles.mainContentText}>
						영어 단어를 보고, 알맞은 한글 뜻을 선택하세요.
					</Grid>
					<Grid className={styles.voca}>{question}</Grid>
					<Grid className={styles.exampleSentence}>{exampleSentence}</Grid>

					<Grid className={styles.inputWrapper}>
						<div className={styles.radioAnswerBox} ref={inputRef}>
							<div className="radio">
								<label>
									<input id="answerOptionId1" name="answerRadio" type="radio"
										value={answerOption1}
										checked={answerOptionId === "answerOptionId1"}
										onChange={answerHandler}/>
									<label className={styles.radioAnswerText}>{answerOption1}</label>
								</label>
							</div>
							<div className="radio">
								<label>
									<input id="answerOptionId2" name="answerRadio" type="radio"
										value={answerOption2}
										checked={answerOptionId === "answerOptionId2"}
										onChange={answerHandler}/>
									<label className={styles.radioAnswerText}>{answerOption2}</label>
								</label>
							</div>
							<div className="radio">
								<label>
									<input id="answerOptionId3" name="answerRadio" type="radio"
										value={answerOption3}
										checked={answerOptionId === "answerOptionId3"}
										onChange={answerHandler}/>
									<label className={styles.radioAnswerText}>{answerOption3}</label>
								</label>
							</div>
							<div className="radio">
								<label>
									<input id="answerOptionId4" name="answerRadio" type="radio"
										value={answerOption4}
										checked={answerOptionId === "answerOptionId4"}
										onChange={answerHandler}/>
									<label className={styles.radioAnswerText}>{answerOption4}</label>
								</label>
							</div>
							<div className="radio">
								<label>
									<input id="answerOptionId5" name="answerRadio" type="radio"
										value={answerOption5}
										checked={answerOptionId === "answerOptionId5"}
										onChange={answerHandler}/>
									<label className={styles.radioAnswerText}>{answerOption5}</label>
								</label>
							</div>
						</div>
					</Grid>

					{toggleVisibility && (
						<img
							src={
								nextBtn === 'hover'
									? nextBtnHover
									: nextBtn === 'clicked'
									? nextBtnClicked
									: nextBtnNormal
							}
							alt="next"
							className={styles.nextBtn}
							onMouseEnter={() => setNextbtn('hover')}
							onMouseLeave={() => setNextbtn('default')}
							onClick={nextQuestion}
						/>
					)}

					<FinishModal
						open1={open1}
						onClickFinish={onClickFinish}
						handleClose1={handleClose1}
					/>
					<InputModal
						input1={input1}
						onClickInput={onClickInput}
						handleInput={handleInput}
					/>
				</form>

				<LineChart examResult={examResult} level={level} />
				<Grid className={styles.footer}></Grid>
			</Grid>
		</Grid>
	);
};

export default withRouter(Main);
