import React, {useState, useEffect} from 'react';
import axios from "../../axios/axios"
import MobileStepper from '@material-ui/core/MobileStepper';
import {withRouter} from 'react-router-dom';
import Steps from 'components/Steps/Steps';

import useStyles from './style';
import styles from './Result.module.css';

import studyBtn from 'Images/Result/study_btn.png';
import studyBtnHover from 'Images/Result/next-btn-h.png';
import studyBtnClicked from 'Images/Result/next-btn-p.png';
import retryBtnHover from 'Images/Result/retry-btn-h.png';
import retryBtnClicked from 'Images/Result/return-btn-p.png';
import retryBtn from 'Images/Result/retry_btn.png';
import cefrBtn from 'Images/Result/CEFR_btn.png';
import upArrow from 'Images/Result/up_arrow.png';
import infoImg from 'Images/Result/info_icon.png';

import bubble1 from 'Images/Result/voca_bubble_1.png';
import bubble2 from 'Images/Result/voca_bubble_2.png';
import bubble3 from 'Images/Result/voca_bubble_3.png';
import bubble4 from 'Images/Result/voca_bubble_4.png';
import bubble5 from 'Images/Result/voca_bubble_5.png';
import bubble6 from 'Images/Result/voca_bubble_6.png';
import bubble7 from 'Images/Result/voca_bubble_7.png';
import bubble8 from 'Images/Result/voca_bubble_8.png';
import bubble9 from 'Images/Result/voca_bubble_9.png';
import bubble10 from 'Images/Result/voca_bubble_10.png';

import graphLv1 from 'Images/Result/graph_set_Lv1.png';
import graphLv2 from 'Images/Result/graph_set_Lv2.png';
import graphLv3 from 'Images/Result/graph_set_Lv3.png';
import graphLv4 from 'Images/Result/graph_set_Lv4.png';
import graphLv5 from 'Images/Result/graph_set_Lv5.png';
import graphLv6 from 'Images/Result/graph_set_Lv6.png';
import graphLv7 from 'Images/Result/graph_set_Lv7.png';
import graphLv8 from 'Images/Result/graph_set_Lv8.png';
import graphLv9 from 'Images/Result/graph_set_Lv9.png';

// 레벨별 이미지 배열
const BUBBLE_IMAGES = [bubble1, bubble2, bubble3, bubble4, bubble5, bubble6, bubble7, bubble8, bubble9, bubble10];
const GRAPH_IMAGES = [graphLv1, graphLv2, graphLv3, graphLv4, graphLv5, graphLv6, graphLv7, graphLv8, graphLv9];

// 레벨별 텍스트 매핑
const LEVEL_TEXT_MAP = [
	'초등 기본', '초등 완성', '중등 기본', '중등 발전', '중등 완성',
	'고등 기본', '고등 발전', '수능 완성', 'voca master'
];

// 서수 접미사 반환
const getOrdinalSuffix = (num) => {
	if (num === 1) return 'st';
	if (num === 2) return 'nd';
	if (num === 3) return 'rd';
	return 'th';
};

// 레벨 텍스트 반환
const getLevelText = (level) => LEVEL_TEXT_MAP[level] || 'voca master';

// 레벨에 해당하는 이미지 반환
const getBubbleImage = (level) => BUBBLE_IMAGES[Math.min(level, BUBBLE_IMAGES.length - 1)];
const getGraphImage = (level) => GRAPH_IMAGES[Math.min(level, GRAPH_IMAGES.length - 1)];

// IRT theta를 어휘 수준(0-9)으로 변환
// theta 범위: -3 ~ +3, 어휘 수준: 0 ~ 9
const thetaToLevel = (theta) => {
	// theta -3 → level 0, theta +3 → level 9
	const normalized = (theta + 3) / 6; // 0 ~ 1
	return Math.max(0, Math.min(9, Math.round(normalized * 9)));
};

// IRT theta를 예상 어휘 수로 변환
// theta 범위: -3 ~ +3, 어휘 수: 0 ~ 9000
const thetaToVocabCount = (theta) => {
	const normalized = (theta + 3) / 6; // 0 ~ 1
	return Math.max(0, Math.min(9000, Math.round(normalized * 9000)));
};

function Result({history, examId, seqNo}) {
	const [level, setLevel] = useState(0);
	const [theta, setTheta] = useState(0);
	const [standardError, setStandardError] = useState(0);
	const [vocabCount, setVocabCount] = useState(0);
	const [totalQuestions, setTotalQuestions] = useState(0);
	const [correctCount, setCorrectCount] = useState(0);
	const [retryButtonType, setRetryButtonType] = useState('default');
	const [studyButtonType, setStudyButtonType] = useState('default');

	const level1 = level;
	const levelStr = `${Math.max(0, vocabCount - 500)} ~ ${vocabCount}개`;
	const classes = useStyles();

	useEffect(() => {
		const fetchResult = async () => {
			try {
				const id = examId || seqNo;
				const response = await axios.get(`/api/irt/exam/${id}/result`);
				const result = response.data;

				// IRT 결과에서 theta, SE 추출
				const thetaValue = result.theta || result.finalTheta || 0;
				const seValue = result.standardError || result.se || 0.5;
				const questions = result.totalQuestions || result.itemCount || 0;
				const correct = result.correctCount || 0;

				setTheta(thetaValue);
				setStandardError(seValue);
				setTotalQuestions(questions);
				setCorrectCount(correct);

				// theta를 레벨과 어휘 수로 변환
				const calculatedLevel = thetaToLevel(thetaValue);
				const calculatedVocab = thetaToVocabCount(thetaValue);

				setLevel(calculatedLevel);
				setVocabCount(calculatedVocab);

				console.log('IRT Result:', { theta: thetaValue, se: seValue, level: calculatedLevel, vocab: calculatedVocab });
			} catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error('Result fetch error:', error.response || error);
				}
			}
		};
		fetchResult();
	}, [examId, seqNo]);

	return (
		<div className={classes.resultWrapper}>
			<div className={classes.navWrapper}>
				<div className={styles.navText}>VOCABULARY TEST RESULT</div>
			</div>
			<div className={classes.resultBodyWrapper}>
				<div className={classes.resultSteps}>
					<Steps />
				</div>
				<div className={classes.resultBodyContent}>
					<div className={classes.resultCard1}>
						<div className={classes.card1HeaderText}>
							현재 알고 있는 예상 단어 수
						</div>
						<div className={classes.bubbleWrapper}>
							<div className={styles.bubbleText}>{levelStr}</div>
							<img
								src={getBubbleImage(level1)}
								alt='bubbleImg'
								className={classes.bubbleImg}
							/>
						</div>
						<div className={classes.leftCardProgressBar}>
							<MobileStepper
								variant='progress'
								steps={10}
								position='static'
								activeStep={level1}
								className={classes.resultProgressBar}
							/>
						</div>
						<div className={classes.progressBarRange}>
							<span className={classes.rangeMin}>0</span>
							<span className={classes.rangeMax}>9000+</span>
						</div>
						<img
							src={infoImg}
							alt={infoImg}
							className={classes.card1InfoImg}
						/>
						<div className={classes.card1ContentText}>
							{levelStr}의 단어를 <br />
							알고 있는 것으로 판단돼요.
						</div>
					</div>
					<div className={classes.resultCard2}>
						<div className={classes.card2HeaderText}>
							어휘력 비교 분석
						</div>
						<img
							src={cefrBtn}
							alt={cefrBtn}
							className={classes.cefrBtn}
						/>
						<div className={classes.card2ContentText}>
							{getLevelText(level1)}에 해당하는 어휘력을 갖고
							있어요.
						</div>
						<img
							src={getGraphImage(level1)}
							alt='graphImg'
							className={classes.graphImg}
						/>
					</div>
					<div className={classes.resultCard3}>
						<div className={classes.card3HeaderText}>
							어휘 학습 로드맵
						</div>
						<div className={classes.loadMapWrapper}>
							<div className={styles.loadMapBubble1}>
								미국 유학 급(GRE, etc)
							</div>
							<img
								src={upArrow}
								alt={upArrow}
								className={classes.upArrow}
							/>
							<div className={styles.loadMapBubble2}>
								TOEIC, TOEFL, etc
							</div>
							<img
								src={upArrow}
								alt={upArrow}
								className={classes.upArrow}
							/>
							<div className={styles.loadMapBubble3}>
								수능 Master 어휘
							</div>
							<img
								src={upArrow}
								alt={upArrow}
								className={classes.upArrow}
							/>
							<div className={styles.loadMapBubble4}>
								고등 어휘
							</div>
							<img
								src={upArrow}
								alt={upArrow}
								className={classes.upArrow}
							/>
							<div className={styles.loadMapBubble5}>
								중등 어휘
							</div>
							<img
								src={upArrow}
								alt={upArrow}
								className={classes.upArrow}
							/>
							<div className={styles.loadMapBubble6}>
								초등 어휘
							</div>
						</div>
					</div>
				</div>
				<div className={classes.resultBodyWhite}>
					<div className={classes.resultBodyText}>
						<span className={classes.context1}>　Suggestion　</span>
						<p className={styles.context2}>
							{level1}
							{getOrdinalSuffix(level1)} Level의 어휘를 마스터하세요!
						</p>
						<p className={styles.context3}>
							권장기간 : 3개월, 1일 10단어
						</p>
						<div className={classes.buttonWrapper}>
							<img
								src={
									retryButtonType === 'clicked'
										? retryBtnClicked
										: retryButtonType === 'hover'
										? retryBtnHover
										: retryBtn
								}
								alt={retryBtn}
								className={classes.retryBtn}
								onMouseEnter={() => {
									setRetryButtonType('hover');
								}}
								onMouseLeave={() => {
									setRetryButtonType('default');
								}}
								onClick={() => {
									history.push('/');
									window.location.reload();
									setRetryButtonType('clicked');
								}}
							/>
							<img
								src={
									studyButtonType === 'clicked'
										? studyBtnClicked
										: studyButtonType === 'hover'
										? studyBtnHover
										: studyBtn
								}
								alt={studyBtn}
								className={classes.studyBtn}
								onMouseEnter={() => {
									setStudyButtonType('hover');
								}}
								onMouseLeave={() => {
									setStudyButtonType('default');
								}}
								onClick={() => {
									history.push('/wordcard');
									setStudyButtonType('clicked');
								}}
							/>
						</div>
					</div>
				</div>
				<div className={classes.resultFooter}></div>
			</div>
		</div>
	);
}

export default withRouter(Result);
