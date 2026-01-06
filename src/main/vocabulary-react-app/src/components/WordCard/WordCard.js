import React, {useState, useEffect} from 'react';

import axios from "../../axios/axios"
import {withRouter, useLocation} from 'react-router-dom'

import styles from './WordCard.module.css';

import Grid from '@material-ui/core/Grid';
import Paper from '@material-ui/core/Paper';
import Button from '@material-ui/core/Button';
import Backdrop from '@material-ui/core/Backdrop';
import CircularProgress from '@material-ui/core/CircularProgress';

import ExitModal from 'components/QuitModal/QuitModal';
import quitBtn from 'Images/Main/quit_btn1.png';

import nextBtnClicked from 'Images/WordCard/next_btn_pressed.png';
import nextBtnHover from 'Images/WordCard/next_btn_hover.png';
import nextBtnNormal from 'Images/WordCard/next_btn_normal.png';
import prevBtnClicked from 'Images/WordCard/prev_btn_pressed.png';
import prevBtnHover from 'Images/WordCard/prev_btn_hover.png';
import prevBtnNormal from 'Images/WordCard/prev_btn_normal.png';
import ReactCardFlip from 'react-card-flip';

// 레벨별 텍스트 매핑
const LEVEL_TEXT_MAP = [
	'초등 기본', '초등 완성', '중등 기본', '중등 발전', '중등 완성',
	'고등 기본', '고등 발전', '수능 완성', 'voca master'
];

const getLevelText = (level) => LEVEL_TEXT_MAP[level - 1] || 'voca master';

const WordCard = ({
		history,
		seqNo,
		open,
		onClickQuit,
		handleClose
	}) => {

	const location = useLocation();
	const queryParams = new URLSearchParams(location.search);
	const targetLevel = parseInt(queryParams.get('level')) || 0;

	const [nextBtn, setNextbtn] = useState('default');
	const [prevBtn, setPrevbtn] = useState('default');
	const [openLoading, setOpenLoading] = React.useState(false);
	const [isFlipped, setFlipped] = React.useState(false);
	const [isFlippedWord, setFlippedWord] = React.useState(false);
	const [isFlipVertical, setFlippedVertical] = React.useState(false);
	const [cardWords, setCardWords] = useState([]);
	const [uiCardWord, setUiCardWord] = useState('');
	const [uiCardMeaning, setUiCardMeaning] = useState('');
	const [uiCardEnglish, setUiCardEnglish] = useState('');
	const [uiCardFront, setUiCardFront] = useState('');
	const [uiCardBack, setUiCardBack] = useState('');
	const [currentWord, setCurrentWord] = useState('');
	const [previousWord, setPreviousWord] = useState('');
	const [currentIndex, setCurrentIndex] = useState(0);
	const [studyLevel, setStudyLevel] = useState(0);

	useEffect(() => {
		const fetchWordsByLevel = async () => {
			setOpenLoading(true);

			try {
				// 레벨 기반으로 단어 가져오기
				const res = await axios.get(`/api/irt/words/level/${targetLevel}`);
				const wordsData = res.data.data || [];

				if (wordsData.length === 0) {
					console.warn('No words found for level:', targetLevel);
					alert(`레벨 ${targetLevel}에 해당하는 단어가 없습니다.`);
					setOpenLoading(false);
					return;
				}

				// 단어 목록 섞기
				const shuffledWords = wordsData
					.map(w => ({
						word: w.word,
						meaning: w.korean || w.meaning,
						wordSeqno: w.wordSeqno
					}))
					.sort(() => Math.random() - 0.5);

				setCardWords(shuffledWords);
				setStudyLevel(targetLevel);
				setCurrentIndex(0);

				const firstWord = shuffledWords[0];
				setCurrentWord(firstWord);
				setPreviousWord(firstWord);
				setUiCardWord(firstWord.word);
				setUiCardBack(firstWord.word);

				console.log(`Loaded ${shuffledWords.length} words for level ${targetLevel}`);

			} catch (err) {
				console.error('Failed to fetch words by level:', err);
				alert('단어를 불러오는데 실패했습니다.');
			}

			setOpenLoading(false);
		};

		const fetchExamWords = async () => {
			setOpenLoading(true);

			try {
				// 기존 시험 결과에서 단어 가져오기
				const res = await axios.get(`/api/irt/exam/${seqNo}/result`);
				const examData = res.data.data;
				const examDetails = examData.wordExamDetails || [];

				if (examDetails.length === 0) {
					console.warn('No exam details found');
					setOpenLoading(false);
					return;
				}

				const words = examDetails
					.filter(detail => detail.word)
					.map(detail => ({
						word: detail.word.word,
						meaning: detail.word.korean || detail.word.meaning,
						wordSeqno: detail.word.wordSeqno
					}));

				if (words.length === 0) {
					console.warn('No words found in exam details');
					setOpenLoading(false);
					return;
				}

				setCardWords(words);
				setCurrentIndex(0);

				const firstWord = words[0];
				setCurrentWord(firstWord);
				setPreviousWord(firstWord);
				setUiCardWord(firstWord.word);
				setUiCardBack(firstWord.word);

			} catch (err) {
				console.error('Failed to fetch exam result:', err);
				alert('단어 카드를 불러오는데 실패했습니다.');
			}

			setOpenLoading(false);
		};

		// 레벨이 지정되면 레벨 기반으로, 아니면 시험 결과에서 가져오기
		if (targetLevel > 0) {
			fetchWordsByLevel();
		} else if (seqNo && seqNo > 0) {
			fetchExamWords();
		}

	}, [targetLevel, seqNo]);

	/*
	useEffect(() => {
		console.log(cardWords.word);
	}, [cardWords]);
	 */

	useEffect(() => {

		setFlipped(true);
		if (!isFlipVertical) {
			setFlippedWord(!isFlippedWord);
		}

	}, [isFlipVertical]);

	useEffect(() => {
		// isFlipped = true, means it is back of the card being shown
		if (isFlipped) {
			setUiCardEnglish("");
			setUiCardMeaning(uiCardWord);
		}
		else {
			setUiCardMeaning("");
			setUiCardEnglish(uiCardWord);
		}

	}, [isFlipped]);

	useEffect(() => {

		if (isFlippedWord) {
			setUiCardFront("");
			setUiCardBack(uiCardWord);
		}
		else {
			setUiCardBack("");
			setUiCardFront(uiCardWord);
		}

	}, [isFlippedWord]);

	const nextWord = async (e) => {
		e.preventDefault();
		setNextbtn('clicked');

		if (cardWords.length === 0) {
			setNextbtn('default');
			return;
		}

		const nextIndex = (currentIndex + 1) % cardWords.length;
		const word = cardWords[nextIndex];

		setCurrentIndex(nextIndex);
		setPreviousWord(currentWord);
		setCurrentWord(word);
		setUiCardWord(word.word);

		setFlippedWord(!isFlippedWord);
		setFlippedVertical(false);

		setNextbtn('default');
	};

	const prevWord = async (e) => {
		e.preventDefault();
		setPrevbtn('clicked');

		if (cardWords.length === 0) {
			setPrevbtn('default');
			return;
		}

		const prevIndex = currentIndex === 0 ? cardWords.length - 1 : currentIndex - 1;
		const word = cardWords[prevIndex];

		setCurrentIndex(prevIndex);
		setPreviousWord(currentWord);
		setCurrentWord(word);
		setUiCardWord(word.word);

		setFlippedWord(!isFlippedWord);
		setFlippedVertical(false);

		setPrevbtn('default');
	};

	const flipWord = async (e) => {
		e.preventDefault();

		if (currentWord.word.localeCompare(uiCardWord) === 0) {
			setUiCardWord(currentWord.meaning);
		} else {
			setUiCardWord(currentWord.word);
		}

		setFlipped(!isFlipped);
		setFlippedVertical(true);
	};

	return (
	<div>
		<Grid container
			spacing={0}
			alignItems="center"
			justify="center">
			<Grid className={styles.navWrapper}>
				<Grid className={styles.navText}>
					{studyLevel > 0
						? `VOCABULARY STUDY - Level ${studyLevel} (${getLevelText(studyLevel)})`
						: 'VOCABULARY STUDY'}
				</Grid>
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
			<Grid className={styles.paperParent}>
			  	{isFlipVertical && (
				    <ReactCardFlip isFlipped={isFlipped} flipDirection="vertical">
						<Paper elevation={12} className={styles.paper}
				 			style={{
				 				display: "flex",
					 		    justifyContent: "center",
					 		    alignItems: "center",
					 		    textAlign: "center",
					 		    verticalAlign: "middle",
					 		    borderRadius: "25px"
				 		    }}>
				 			{uiCardEnglish}
				 		</Paper>
				 		<Paper elevation={12} className={styles.paper}
				 			style={{
				 				color: "white",
				 				background: "#68c8c7",
				 				display: "flex",
					 		    justifyContent: "center",
					 		    alignItems: "center",
					 		    textAlign: "center",
					 		    verticalAlign: "middle",
					 		    borderRadius: "25px"
				 		    }}>
				 			{uiCardMeaning}
				 		</Paper>
					</ReactCardFlip>
			  	)}
			  	{!isFlipVertical && (
				    <ReactCardFlip isFlipped={isFlippedWord} flipDirection="horizontal">
						<Paper elevation={12} className={styles.paper}
				 			style={{
				 				display: "flex",
					 		    justifyContent: "center",
					 		    alignItems: "center",
					 		    textAlign: "center",
					 		    verticalAlign: "middle",
					 		    borderRadius: "25px"
				 		    }}>
				 			{uiCardFront}
				 		</Paper>
				 		<Paper elevation={12} className={styles.paper}
				 			style={{
				 				display: "flex",
					 		    justifyContent: "center",
					 		    alignItems: "center",
					 		    textAlign: "center",
					 		    verticalAlign: "middle",
					 		    borderRadius: "25px"
				 		    }}>
				 			{uiCardBack}
				 		</Paper>
					</ReactCardFlip>
			  	)}
			</Grid>
			<Grid container spacing={2}>
			  <Grid item xs={6}>
			  	<img
			  		src={
						prevBtn === 'hover'
							? prevBtnHover
							: prevBtn === 'clicked'
							? prevBtnClicked
							: prevBtnNormal
					}
					alt={prevBtnNormal}
					onMouseEnter={() => {
						setPrevbtn('hover');
					}}
					onMouseLeave={() => {
						setPrevbtn('default');
					}}
			  		onClick={prevWord}
				/>
			  </Grid>
			  <Grid item xs={6}>
			  	<img
			  		src={
						nextBtn === 'hover'
							? nextBtnHover
							: nextBtn === 'clicked'
							? nextBtnClicked
							: nextBtnNormal
					}
					alt={nextBtnNormal}
					onMouseEnter={() => {
						setNextbtn('hover');
					}}
					onMouseLeave={() => {
						setNextbtn('default');
					}}
			  		onClick={nextWord}
				/>
			  </Grid>
			  <Grid item xs={12} className={styles.fontBig}>
				  <Button className={styles.flipButton} onClick={flipWord}>
				  	Flip
				  </Button>
			  </Grid>
			  <Grid item xs={12} style={{ textAlign: 'center', marginTop: '1rem' }}>
				  <span style={{ fontSize: '1.5rem', color: '#666' }}>
					  {cardWords.length > 0 ? `${currentIndex + 1} / ${cardWords.length}` : ''}
				  </span>
			  </Grid>
			</Grid>
		</Grid>
		<Backdrop sx={{ color: '#fff' }} className={styles.loading} open={openLoading}>
	    	<CircularProgress color="inherit" />
	    </Backdrop>
	    <Grid className={styles.footer}></Grid>
	</div>
	);
};

export default withRouter(WordCard);
