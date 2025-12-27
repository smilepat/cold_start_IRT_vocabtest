import React, {useState, useEffect, useRef} from 'react';

import axios from "../../axios/axios"
import {withRouter} from 'react-router-dom'
import {useMediaQuery} from 'react-responsive'

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

const WordCard = ({
		history,
		seqNo,
		open,
		onClickQuit,
		handleClose
	}) => {

	//const isDesktopOrLaptop = useMediaQuery({ query: '(min-width: 1224px)' })
	//const isBigScreen = useMediaQuery({ query: '(min-width: 1824px)' })
	//const isTabletOrMobile = useMediaQuery({ query: '(max-width: 1224px)' })
	//const isPortrait = useMediaQuery({ query: '(orientation: portrait)' })
	//const isRetina = useMediaQuery({ query: '(min-resolution: 2dppx)' })

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

	useEffect(() => {
		const handleFetchResult = async () => {

			setOpenLoading(true);

			try {
				// Fetch exam result from IRT CAT API
				const res = await axios.get(`/api/irt/exam/${seqNo}/result`);
				const examData = res.data;
				const examDetails = examData.wordExamDetails || [];

				if (examDetails.length === 0) {
					console.warn('No exam details found');
					setOpenLoading(false);
					return;
				}

				// Extract words from exam details for card study
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
				const initIndex = Math.floor(Math.random() * words.length);

				setCurrentWord(words[initIndex]);
				setPreviousWord(words[initIndex]);
				setUiCardWord(words[initIndex].word);
				setUiCardBack(words[initIndex].word);

			} catch (err) {
				console.error('Failed to fetch exam result:', err);
				alert('단어 카드를 불러오는데 실패했습니다.');
			}

			setOpenLoading(false);
		};

		if (seqNo && seqNo > 0) {
			handleFetchResult();
		}

	}, [seqNo]);

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

		var index = Math.floor(Math.random() * cardWords.length);
		var word = cardWords[index];

		if (word.word.localeCompare(previousWord.word) === 0) {
			index = Math.floor(Math.random() * cardWords.length);
			word = cardWords[index];
		}

		setCurrentWord(word);
		setPreviousWord(currentWord);
		setUiCardWord(word.word);

		setFlippedWord(!isFlippedWord);
		setFlippedVertical(false);

		setNextbtn('default');
	};

	const prevWord = async (e) => {
		e.preventDefault();
		setPrevbtn('clicked');

		var index = Math.floor(Math.random() * cardWords.length);
		var word = cardWords[index];

		if (word.word.localeCompare(previousWord.word) === 0) {
			index = Math.floor(Math.random() * cardWords.length);
			word = cardWords[index];
		}

		setCurrentWord(word);
		setPreviousWord(currentWord);
		setUiCardWord(word.word);

		if (isFlippedWord)

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
