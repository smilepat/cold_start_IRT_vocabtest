import React, {useState, useEffect} from 'react';
import axios from 'axios';
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

function Result({history, seqNo}) {
	const [level, setLevel] = useState(0);
	const [retryButtonType, setRetryButtonType] = useState('default');
	const [studyButtonType, setStudyButtonType] = useState('default');
	let levelStr = '';
	const classes = useStyles();

	const levelNth = function (level) {
		if (level > 3) return 'th';
		switch (level % 10) {
			case 1:
				return 'st';
			case 2:
				return 'nd';
			case 3:
				return 'rd';
			default:
				return 'th';
		}
	};

	const levelToText = (level) => {
		return level === 0
			? '초등 기본'
			: level === 1
			? '초등 완성'
			: level === 2
			? '중등 기본'
			: level === 3
			? '중등 발전'
			: level === 4
			? '중등 완성'
			: level === 5
			? '고등 기본'
			: level === 6
			? '고등 발전'
			: level === 7
			? '수능 완성'
			: level === 8
			? 'voca master'
			: 'voca master';
	};

	useEffect(() => {
		const res = async () => {
			try {
				let response = await axios.post(`/api/word-exams/${seqNo}`);
				let level = response.data.data.examLevel;
				setLevel(level);
				console.log('level', level);
			} catch (error) {
				console.log(error.response);
			}
		};
		res();
	}, []);

	levelStr = `${(level - 1) * 1000} ~ ${level * 1000}개`;

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
								src={
									level === 0
										? bubble1
										: level === 1
										? bubble2
										: level === 2
										? bubble3
										: level === 3
										? bubble4
										: level === 4
										? bubble5
										: level === 5
										? bubble6
										: level === 6
										? bubble7
										: level === 7
										? bubble8
										: level === 8
										? bubble9
										: level === 9
										? bubble10
										: bubble10
								}
								alt='bubbleImg'
								className={classes.bubbleImg}
							/>
						</div>
						<div className={classes.leftCardProgressBar}>
							<MobileStepper
								variant='progress'
								steps={10}
								position='static'
								activeStep={level}
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
							{levelToText(level)}에 해당하는 어휘력을 갖고
							있어요.
						</div>
						<img
							src={
								level === 0
									? graphLv1
									: level === 1
									? graphLv2
									: level === 2
									? graphLv3
									: level === 3
									? graphLv4
									: level === 4
									? graphLv5
									: level === 5
									? graphLv6
									: level === 6
									? graphLv7
									: level === 7
									? graphLv8
									: level === 8
									? graphLv9
									: graphLv9
							}
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
							{level}
							{levelNth(level)} 1000 Level의 어휘를 마스터하세요!
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
