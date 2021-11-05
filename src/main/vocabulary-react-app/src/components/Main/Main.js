import React, {useState, useEffect, useRef} from 'react';

import axios from "axios/axios"
import {withRouter} from 'react-router-dom';

import Grid from '@material-ui/core/Grid';

import styles from './Main.module.css';

import Steps from 'components/Steps/Steps';
import FinishModal from '../FinishModal/FinishModal';
import ExitModal from 'components/QuitModal/QuitModal';
import LineChart from 'components/LineChart/LineChart';
import ProgressContainer from 'components/ProgressNew/ProgressNew';

import quitBtn from 'Images/Main/quit_btn1.png';
import skipBtn from 'Images/Main/skip_btn3.png';
import nextBtnClicked from 'Images/Main/next_btn.png';
import nextBtnHover from 'Images/Main/skip-btn-1-h.png';
import nextBtnNormal from 'Images/Main/skip-btn-1-n.png';

const Main = ({
	history,
	seqNo,
	open1,
	open,
	onClickQuit,
	onClickFinish,
	handleClose,
	handleClose1,
}) => {
	const [activeStep, setActiveStep] = React.useState(0);
	const [counter, setCounter] = useState(1);
	const [done, setDone] = useState(false);
	const [examResult, setExamResult] = useState([]);
	const [exampleSentence, setExampleSentence] = useState('');
	const [inputAnswer, setInputAnswer] = useState('');
	const [question, setQuestion] = useState('');
	const [level, setLevel] = useState(0);
	const [skipped, setSkipped] = React.useState(new Set());
	const [toggleVisibility, setToggleVisibility] = useState(false);
	const [toggleShadow, setToggleShadow] = useState(false);
	const [inputLength, setInputLength] = useState(0);
	const [nextBtn, setNextbtn] = useState('default');

	const isStepSkipped = (step) => {
		return skipped.has(step);
	};

	let steps = useRef(null);
	const inputRef = useRef();

	useEffect(() => {
		if (done === true) {
			onClickFinish();
		}
	}, [done]);
	useEffect(() => {
		const res = async () => {
			try {
				if (done === true || seqNo === 0) return;
				let response = await axios.get(
					`/api/word-exams/${seqNo}/orders/${counter}`,
				);
				console.log('response after posting', response);
				const {
					word,
					level,
					exampleSentence: sentence,
				} = response.data.data;
				setQuestion(word);
				if (counter === 1) {
					steps.current = level;
				}
				setLevel(level);
				setExampleSentence(sentence);
				setQuestion(word);
				inputRef.current.focus();
			} catch (error) {
				console.error(error);
			}
		};
		res();
	}, [counter]);

	useEffect(() => {
		const handleFetchResult = async () => {
			if (counter <= 1) return;
			try {
				let res = await axios.get(`/api/word-exams/${seqNo}`);
				console.log('each result', res);
				setExamResult(res.data.data.wordExamDetails);
				console.log('examEnd', done);
			} catch (err) {
				alert('Error occured');
			}
		};
		handleFetchResult();
	}, [counter, seqNo]);

	const nextQuestion = async (e) => {
		e.preventDefault();
		let newSkipped = skipped;
		if (isStepSkipped(activeStep)) {
			newSkipped = new Set(newSkipped.values());
			newSkipped.delete(activeStep);
		}

		setActiveStep((prevActiveStep) => prevActiveStep + 1);
		setSkipped(newSkipped);
		try {
			if (done === false) {
				const result = await axios.post(
					`/api/word-exams/${seqNo}/orders/${counter}`,
					{
						answer: `${inputAnswer}`,
						word: `${question}`,
					},
					{
						headers: {
							Accept: 'application/json, text/plain, */*',
						},
					},
				);
				const isExamEnd = result.data.data.isExamEnd;
				setInputAnswer('');
				console.log({inputAnswer});
				setDone(isExamEnd);
				setNextbtn('clicked');
				if (done === false) {
					setCounter(counter + 1);
				}
				console.log('result', result);
			}
		} catch (err) {
			alert('err', err);
		}
	};
	useEffect(() => {
		setToggleShadow(false);
		setNextbtn('default');
	}, [counter]);

	const inputHandler = (e) => {
		setInputAnswer(e.target.value);
		setInputLength(e.target.value.length);
		if (counter === 1) {
			e.target.value.length >= 1
				? setToggleVisibility(true)
				: setToggleVisibility(false);
		}
		e.target.value.length > 1
			? setToggleShadow(true)
			: setToggleShadow(false);
	};
	let getSteps = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

	return (
		<Grid container className={styles.mainWrapper}>
			<Grid className={styles.navWrapper}>
				<Grid className={styles.navText}>VOCABULARY TEST</Grid>
				<Grid className={styles.modalWrapper}>
					<img
						src={quitBtn}
						alt={quitBtn}
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

				<form
					className={styles.mainBodyContent}
					onSubmit={nextQuestion}
				>
					<ProgressContainer
						done={done}
						counter={counter}
						getSteps={getSteps}
						activeStep={activeStep}
						isStepSkipped={isStepSkipped}
					/>
					{done === false ? (
						<img
							height='60'
							width='150'
							src={skipBtn}
							alt={skipBtn}
							onClick={nextQuestion}
							className={styles.skipBtn}
						/>
					) : (
						<img
							style={{
								width: 150,
								height: 60,
								visibility: 'hidden',
							}}
						/>
					)}
					<Grid className={styles.mainContentText}>
						영어 단어를 보고, 알맞은 한글 뜻을 입력해주세요.
					</Grid>
					<Grid className={styles.voca}>{question}</Grid>

					<Grid className={styles.exampleSentence}>
						{exampleSentence}
					</Grid>
					<Grid className={styles.inputWrapper}>
						<input
							value={inputAnswer}
							ref={inputRef}
							onChange={inputHandler}
							className={
								toggleShadow === true
									? `${styles.inputAnswer}`
									: `${styles.inputAnswer1}`
							}
							disabled={done === true}
							type='text'
							placeholder='한글 뜻을 입력해주세요.'
							autoFocus
						/>
					</Grid>
					{toggleVisibility === true ? (
						<img
							src={
								nextBtn === 'hover'
									? nextBtnHover
									: nextBtn === 'clicked'
									? nextBtnClicked
									: nextBtnNormal
							}
							alt={nextBtnNormal}
							className={styles.nextBtn}
							onMouseEnter={() => {
								setNextbtn('hover');
							}}
							onMouseLeave={() => {
								setNextbtn('default');
							}}
							onClick={nextQuestion}
						/>
					) : null}
					<FinishModal
						open1={open1}
						onClickFinish={onClickFinish}
						handleClose1={handleClose1}
					/>
				</form>
				<LineChart examResult={examResult} level={level} />

				<Grid className={styles.footer}></Grid>
			</Grid>
		</Grid>
	);
};

export default withRouter(Main);
