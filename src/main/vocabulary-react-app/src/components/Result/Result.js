import React, {useState, useEffect} from 'react';
import axios from "../../axios/axios"
import MobileStepper from '@material-ui/core/MobileStepper';
import {withRouter} from 'react-router-dom';
import Steps from 'components/Steps/Steps';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import Button from '@material-ui/core/Button';
import Radio from '@material-ui/core/Radio';
import RadioGroup from '@material-ui/core/RadioGroup';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import SchoolIcon from '@material-ui/icons/School';
import TimelineIcon from '@material-ui/icons/Timeline';
import Divider from '@material-ui/core/Divider';

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

// 학습 계획 생성 함수
const generateStudyPlan = (currentLevel, targetLevel, wordsPerLevel = 100) => {
	const plans = [];
	const levelsToStudy = targetLevel - currentLevel;

	if (levelsToStudy <= 0) {
		return [{
			phase: 1,
			title: '현재 레벨 복습',
			level: currentLevel,
			levelName: getLevelText(currentLevel - 1),
			words: wordsPerLevel,
			dailyWords: 10,
			days: Math.ceil(wordsPerLevel / 10),
			description: '현재 레벨의 어휘를 완벽히 마스터하세요.'
		}];
	}

	for (let i = 0; i < levelsToStudy; i++) {
		const studyLevel = currentLevel + i + 1;
		plans.push({
			phase: i + 1,
			title: `${i + 1}단계: ${getLevelText(studyLevel - 1)}`,
			level: studyLevel,
			levelName: getLevelText(studyLevel - 1),
			words: wordsPerLevel,
			dailyWords: 10,
			days: Math.ceil(wordsPerLevel / 10),
			weeks: Math.ceil(wordsPerLevel / 10 / 7),
			description: i === 0
				? '먼저 이 레벨부터 시작하세요!'
				: `${getLevelText(studyLevel - 2)} 완료 후 진행하세요.`
		});
	}

	return plans;
};

function Result({history, examId, seqNo}) {
	const [level, setLevel] = useState(0);
	const [vocabCount, setVocabCount] = useState(0);
	const [retryButtonType, setRetryButtonType] = useState('default');
	const [studyButtonType, setStudyButtonType] = useState('default');
	const [goalModalOpen, setGoalModalOpen] = useState(false);
	const [selectedGoalLevel, setSelectedGoalLevel] = useState(1);
	const [maxLevel, setMaxLevel] = useState(9);
	const [menuOpen, setMenuOpen] = useState(false);
	const [studyPlanModalOpen, setStudyPlanModalOpen] = useState(false);
	const [studyPlanTarget, setStudyPlanTarget] = useState(9);
	const [wordCounts, setWordCounts] = useState({});

	const level1 = level;
	const levelStr = `${Math.max(0, vocabCount - 500)} ~ ${vocabCount}개`;
	const classes = useStyles();

	useEffect(() => {
		const fetchResult = async () => {
			try {
				const id = examId || seqNo;
				const response = await axios.get(`/api/irt/exam/${id}/result`);
				const result = response.data.data;

				// IRT 결과에서 theta 추출
				const thetaValue = result.theta || result.finalTheta || 0;

				// theta를 레벨과 어휘 수로 변환
				const calculatedLevel = thetaToLevel(thetaValue);
				const calculatedVocab = thetaToVocabCount(thetaValue);

				setLevel(calculatedLevel);
				setVocabCount(calculatedVocab);
				setSelectedGoalLevel(Math.min(calculatedLevel + 1, 9));

				console.log('IRT Result:', { theta: thetaValue, level: calculatedLevel, vocab: calculatedVocab });
			} catch (error) {
				if (process.env.NODE_ENV === 'development') {
					console.error('Result fetch error:', error.response || error);
				}
			}
		};

		const fetchMaxLevel = async () => {
			try {
				const response = await axios.get('/api/irt/words/max-level');
				setMaxLevel(response.data.data || 9);
			} catch (error) {
				console.error('Max level fetch error:', error);
			}
		};

		fetchResult();
		fetchMaxLevel();
	}, [examId, seqNo]);

	// 레벨별 단어 수 가져오기
	useEffect(() => {
		const fetchWordCounts = async () => {
			const counts = {};
			for (let lvl = 1; lvl <= maxLevel; lvl++) {
				try {
					const res = await axios.get(`/api/irt/words/level/${lvl}`);
					counts[lvl] = res.data.data?.length || 100;
				} catch (err) {
					counts[lvl] = 100;
				}
			}
			setWordCounts(counts);
		};
		if (maxLevel > 0) {
			fetchWordCounts();
		}
	}, [maxLevel]);

	const handleStudyClick = () => {
		setGoalModalOpen(true);
		setStudyButtonType('clicked');
	};

	const handleGoalConfirm = () => {
		setGoalModalOpen(false);
		history.push(`/wordcard?level=${selectedGoalLevel}`);
	};

	const handleGoalCancel = () => {
		setGoalModalOpen(false);
		setStudyButtonType('default');
	};

	const handleMenuOpen = () => {
		setMenuOpen(true);
	};

	const handleMenuClose = () => {
		setMenuOpen(false);
	};

	const handleStudyPlanOpen = () => {
		setStudyPlanTarget(maxLevel);
		setStudyPlanModalOpen(true);
		setMenuOpen(false);
	};

	const handleStudyPlanClose = () => {
		setStudyPlanModalOpen(false);
	};

	const studyPlans = generateStudyPlan(level1, studyPlanTarget, wordCounts[level1 + 1] || 100);
	const totalDays = studyPlans.reduce((sum, plan) => sum + (wordCounts[plan.level] ? Math.ceil(wordCounts[plan.level] / 10) : plan.days), 0);
	const totalWeeks = Math.ceil(totalDays / 7);

	return (
		<div className={classes.resultWrapper}>
			<div className={classes.navWrapper}>
				<div className={styles.navText}>VOCABULARY TEST RESULT</div>
				{/* 메뉴 버튼 */}
				<IconButton
					onClick={handleMenuOpen}
					style={{
						position: 'absolute',
						right: '3rem',
						top: '4rem',
						backgroundColor: 'white',
						boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
					}}
				>
					<MenuIcon style={{ fontSize: '3rem', color: '#68c8c7' }} />
				</IconButton>
			</div>

			{/* 사이드 메뉴 Drawer */}
			<Drawer anchor="right" open={menuOpen} onClose={handleMenuClose}>
				<div style={{ width: '300px', padding: '1rem' }}>
					<div style={{
						backgroundColor: '#68c8c7',
						color: 'white',
						padding: '2rem',
						marginBottom: '1rem',
						borderRadius: '8px',
						textAlign: 'center'
					}}>
						<SchoolIcon style={{ fontSize: '4rem' }} />
						<div style={{ fontSize: '1.8rem', fontWeight: 'bold', marginTop: '0.5rem' }}>
							학습 메뉴
						</div>
						<div style={{ fontSize: '1.2rem', marginTop: '0.5rem' }}>
							현재 레벨: {getLevelText(level1)}
						</div>
					</div>
					<Divider />
					<List>
						<ListItem button onClick={handleStudyPlanOpen}>
							<ListItemIcon>
								<TimelineIcon style={{ fontSize: '2.5rem', color: '#68c8c7' }} />
							</ListItemIcon>
							<ListItemText
								primary={<span style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>학습 계획 보기</span>}
								secondary={<span style={{ fontSize: '1.1rem' }}>목표까지의 학습 로드맵</span>}
							/>
						</ListItem>
						<ListItem button onClick={() => { setMenuOpen(false); handleStudyClick(); }}>
							<ListItemIcon>
								<SchoolIcon style={{ fontSize: '2.5rem', color: '#68c8c7' }} />
							</ListItemIcon>
							<ListItemText
								primary={<span style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>학습 시작</span>}
								secondary={<span style={{ fontSize: '1.1rem' }}>목표 레벨 선택 후 학습</span>}
							/>
						</ListItem>
					</List>
				</div>
			</Drawer>
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
								onClick={handleStudyClick}
							/>
						</div>

						{/* 목표 레벨 선택 모달 */}
						<Dialog
							open={goalModalOpen}
							onClose={handleGoalCancel}
							maxWidth="sm"
							fullWidth
						>
							<DialogTitle style={{ backgroundColor: '#68c8c7', color: 'white', textAlign: 'center' }}>
								학습 목표 설정
							</DialogTitle>
							<DialogContent style={{ padding: '2rem' }}>
								<p style={{ fontSize: '1.4rem', marginBottom: '1rem', textAlign: 'center' }}>
									현재 레벨: <strong>{getLevelText(level1)}</strong> (Level {level1})
								</p>
								<p style={{ fontSize: '1.2rem', marginBottom: '2rem', textAlign: 'center', color: '#666' }}>
									학습할 목표 레벨을 선택하세요
								</p>
								<RadioGroup
									value={selectedGoalLevel.toString()}
									onChange={(e) => setSelectedGoalLevel(parseInt(e.target.value))}
									style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}
								>
									{Array.from({ length: maxLevel }, (_, i) => i + 1).map((lvl) => (
										<FormControlLabel
											key={lvl}
											value={lvl.toString()}
											control={<Radio color="primary" />}
											label={
												<span style={{ fontSize: '1.3rem' }}>
													Level {lvl} - {getLevelText(lvl - 1)}
													{lvl === level1 + 1 && ' (추천)'}
												</span>
											}
											style={{
												backgroundColor: lvl === level1 + 1 ? '#e8f5f5' : 'transparent',
												borderRadius: '8px',
												padding: '0.5rem',
												margin: '0'
											}}
										/>
									))}
								</RadioGroup>
							</DialogContent>
							<DialogActions style={{ padding: '1rem 2rem', justifyContent: 'center' }}>
								<Button
									onClick={handleGoalCancel}
									style={{
										fontSize: '1.2rem',
										padding: '0.8rem 2rem',
										color: '#666'
									}}
								>
									취소
								</Button>
								<Button
									onClick={handleGoalConfirm}
									variant="contained"
									style={{
										fontSize: '1.2rem',
										padding: '0.8rem 2rem',
										backgroundColor: '#68c8c7',
										color: 'white'
									}}
								>
									학습 시작
								</Button>
							</DialogActions>
						</Dialog>

						{/* 학습 계획 모달 */}
						<Dialog
							open={studyPlanModalOpen}
							onClose={handleStudyPlanClose}
							maxWidth="md"
							fullWidth
						>
							<DialogTitle style={{ backgroundColor: '#68c8c7', color: 'white', textAlign: 'center' }}>
								<TimelineIcon style={{ fontSize: '2.5rem', verticalAlign: 'middle', marginRight: '0.5rem' }} />
								맞춤 학습 계획
							</DialogTitle>
							<DialogContent style={{ padding: '2rem' }}>
								{/* 현재 상태 요약 */}
								<div style={{
									backgroundColor: '#f5f5f5',
									padding: '1.5rem',
									borderRadius: '12px',
									marginBottom: '2rem',
									textAlign: 'center'
								}}>
									<div style={{ fontSize: '1.3rem', color: '#666', marginBottom: '0.5rem' }}>
										현재 어휘 수준
									</div>
									<div style={{ fontSize: '2rem', fontWeight: 'bold', color: '#68c8c7' }}>
										Level {level1} - {getLevelText(level1)}
									</div>
									<div style={{ fontSize: '1.2rem', color: '#888', marginTop: '0.5rem' }}>
										예상 어휘: {levelStr}
									</div>
								</div>

								{/* 목표 레벨 선택 */}
								<div style={{ marginBottom: '2rem' }}>
									<div style={{ fontSize: '1.4rem', fontWeight: 'bold', marginBottom: '1rem' }}>
										목표 레벨 설정
									</div>
									<RadioGroup
										row
										value={studyPlanTarget.toString()}
										onChange={(e) => setStudyPlanTarget(parseInt(e.target.value))}
										style={{ justifyContent: 'center', gap: '0.5rem' }}
									>
										{Array.from({ length: maxLevel - level1 }, (_, i) => level1 + i + 1).map((lvl) => (
											<FormControlLabel
												key={lvl}
												value={lvl.toString()}
												control={<Radio color="primary" size="small" />}
												label={<span style={{ fontSize: '1.1rem' }}>Lv.{lvl}</span>}
												style={{
													backgroundColor: studyPlanTarget === lvl ? '#e8f5f5' : '#f9f9f9',
													borderRadius: '8px',
													padding: '0.3rem 0.8rem',
													margin: '0.2rem'
												}}
											/>
										))}
									</RadioGroup>
								</div>

								{/* 학습 계획 상세 */}
								<div style={{ marginBottom: '1rem' }}>
									<div style={{
										fontSize: '1.4rem',
										fontWeight: 'bold',
										marginBottom: '1rem',
										display: 'flex',
										alignItems: 'center',
										justifyContent: 'space-between'
									}}>
										<span>학습 로드맵</span>
										<span style={{ fontSize: '1.1rem', color: '#68c8c7' }}>
											총 {totalDays}일 (약 {totalWeeks}주)
										</span>
									</div>

									<div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
										{studyPlans.map((plan, index) => (
											<div
												key={plan.phase}
												style={{
													backgroundColor: index === 0 ? '#e8f5f5' : '#fafafa',
													border: index === 0 ? '2px solid #68c8c7' : '1px solid #eee',
													borderRadius: '12px',
													padding: '1.5rem',
													position: 'relative'
												}}
											>
												{index === 0 && (
													<div style={{
														position: 'absolute',
														top: '-10px',
														left: '20px',
														backgroundColor: '#68c8c7',
														color: 'white',
														padding: '2px 12px',
														borderRadius: '10px',
														fontSize: '1rem'
													}}>
														시작!
													</div>
												)}
												<div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
													<div>
														<div style={{ fontSize: '1.4rem', fontWeight: 'bold', color: '#333' }}>
															{plan.title}
														</div>
														<div style={{ fontSize: '1.1rem', color: '#666', marginTop: '0.3rem' }}>
															{plan.description}
														</div>
													</div>
													<div style={{ textAlign: 'right' }}>
														<div style={{ fontSize: '1.3rem', fontWeight: 'bold', color: '#68c8c7' }}>
															{wordCounts[plan.level] || plan.words}개 단어
														</div>
														<div style={{ fontSize: '1rem', color: '#888' }}>
															하루 {plan.dailyWords}개 / {Math.ceil((wordCounts[plan.level] || plan.words) / 10)}일
														</div>
													</div>
												</div>
												<Button
													variant="outlined"
													size="small"
													style={{
														marginTop: '1rem',
														borderColor: '#68c8c7',
														color: '#68c8c7',
														fontSize: '1rem'
													}}
													onClick={() => {
														setStudyPlanModalOpen(false);
														history.push(`/wordcard?level=${plan.level}`);
													}}
												>
													이 레벨 학습하기
												</Button>
											</div>
										))}
									</div>
								</div>

								{/* 학습 팁 */}
								<div style={{
									backgroundColor: '#fff9e6',
									padding: '1rem 1.5rem',
									borderRadius: '8px',
									borderLeft: '4px solid #ffc107',
									marginTop: '1.5rem'
								}}>
									<div style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#856404', marginBottom: '0.5rem' }}>
										학습 팁
									</div>
									<ul style={{ margin: 0, paddingLeft: '1.5rem', fontSize: '1.1rem', color: '#666' }}>
										<li>매일 꾸준히 10개씩 학습하세요</li>
										<li>카드를 뒤집어 뜻을 확인하며 암기하세요</li>
										<li>이전 레벨 복습도 함께 진행하면 효과적입니다</li>
									</ul>
								</div>
							</DialogContent>
							<DialogActions style={{ padding: '1rem 2rem', justifyContent: 'center' }}>
								<Button
									onClick={handleStudyPlanClose}
									style={{
										fontSize: '1.2rem',
										padding: '0.8rem 2rem',
										color: '#666'
									}}
								>
									닫기
								</Button>
								<Button
									onClick={() => {
										setStudyPlanModalOpen(false);
										history.push(`/wordcard?level=${level1 + 1}`);
									}}
									variant="contained"
									style={{
										fontSize: '1.2rem',
										padding: '0.8rem 2rem',
										backgroundColor: '#68c8c7',
										color: 'white'
									}}
								>
									첫 단계 학습 시작
								</Button>
							</DialogActions>
						</Dialog>
					</div>
				</div>
				<div className={classes.resultFooter}></div>
			</div>
		</div>
	);
}

export default withRouter(Result);
