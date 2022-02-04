import React, {useState, useEffect, useRef} from 'react';

import axios from "axios/axios"
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
	const [cardWords, setCardWords] = useState([]);
	const [uiCardWord, setUiCardWord] = useState('');
	const [currentWord, setCurrentWord] = useState('');
	const [previousWord, setPreviousWord] = useState('');

	useEffect(() => {
		const handleFetchResult = async () => {

			setOpenLoading(true);

			try {
				const res = await axios.get(`/api/word-exams/${seqNo}`);
				const exams = res.data.data.wordExamDetails;
				const sortedExams = [...exams].sort((a, b) => b.examOrder - a.examOrder)
				const lastExam = sortedExams[0];

				const currIdx = lastExam.wordSeqno;
				var lowIdx = lastExam.wordSeqnoLowLimit;
				var highIdx = lastExam.wordSeqnoHighLimit;

				highIdx = currIdx + 50;
				lowIdx = currIdx - 50;

				// Only can go as high and as low as the range limit
				if (highIdx > lastExam.wordSeqnoHighLimit)
					highIdx = lastExam.wordSeqnoHighLimit
				if (lowIdx < lastExam.wordSeqnoLowLimit)
					lowIdx = lastExam.wordSeqnoLowLimit

				const wordCards = await axios.get(`/api/word-exams/wordcard/${lowIdx}/${highIdx}`);
				setCardWords(wordCards.data.data);
				const initIndex = Math.floor(Math.random() * wordCards.data.data.length);

				setCurrentWord(wordCards.data.data[initIndex]);
				setPreviousWord(wordCards.data.data[initIndex]);
				setUiCardWord(wordCards.data.data[initIndex].word)


			} catch (err) {
				alert('Error occured ' + err);
			}

			setOpenLoading(false);
		};

		handleFetchResult();

	}, [seqNo]);

	useEffect(() => {
		setUiCardWord(cardWords.word);
	}, [cardWords]);

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
		setUiCardWord(word.word)

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
		setUiCardWord(word.word)

		setPrevbtn('default');
	};

	const flipWord = async (e) => {
		if (currentWord.word.localeCompare(uiCardWord) === 0) {
			setUiCardWord(currentWord.meaning);
		} else {
			setUiCardWord(currentWord.word);
		}
	};

	const handleToggleLoading = () => {
		setOpenLoading(!openLoading);
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
			<Grid>
		 		<Paper elevation={12} className={styles.paper}
		 			style={{
		 				display: "flex",
			 		    justifyContent: "center",
			 		    alignItems: "center",
			 		    textAlign: "center",
			 		    verticalAlign: "middle",
			 		    borderRadius: "25px"
		 		    }}>
		 			{uiCardWord}
		 		</Paper>
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
