import React, {useState} from 'react';

import Grid from '@material-ui/core/Grid';

import styles from './Intro.module.css';
import useStyles from './style';

import ground from 'Images/Intro/ground.png';
import introImg from 'Images/Intro/intro_img.png';
import startBtn from 'Images/Intro/start_btn.png';
import startBtnHover from 'Images/Intro/start-btn-h.png';
import startBtnClicked from 'Images/Intro/start-btn-p.png';
import { useMediaQuery } from 'react-responsive'

const Desktop = ({ children }) => {
	const isDesktop = useMediaQuery({ minWidth: 900 })
	return isDesktop ? children : null
  }
  const Tablet = ({ children }) => {
	const isTablet = useMediaQuery({ minWidth: 768, maxWidth: 991 })
	return isTablet ? children : null
  }
  const Mobile = ({ children }) => {
	const isMobile = useMediaQuery({ maxWidth: 899 })
	return isMobile ? children : null
  }
  const Default = ({ children }) => {
	const isNotMobile = useMediaQuery({ minWidth: 768 })
	return isNotMobile ? children : null
  }
  const Portrait = ({ children }) => {
	const isPortrait = useMediaQuery({ query: '(orientation: portrait)' })
	return isPortrait ? children : null	
  }
function Intro({history}) {
	const classes = useStyles();
	const [startButtonType, setStartButtonType] = useState('default');
	return (
		<div className={classes.introWrapper}>
			<Desktop>
				<Grid className={classes.introNavWrapper}>
					<Grid className={styles.introNavText}>VOCABULARY TEST</Grid>
				</Grid>
				<Grid className={classes.introBodyWrapper}>
					<Grid className={styles.introBodyText}>
						시작하기 버튼을 클릭해서 단어테스트를 시작하세요.
					</Grid>
					<Grid className={classes.introBodyContent}>
						<Grid className={classes.introImgWrapper}>
							<img
								src={introImg}
								alt={introImg}
								className={classes.introImg}
							/>
							<img
								src={ground}
								alt={ground}
								className={classes.introGround}
							/>
							+
							<img
								src={
									startButtonType === 'clicked'
										? startBtnClicked
										: startButtonType === 'hover'
										? startBtnHover
										: startBtn
								}
								alt={startBtn}
								className={classes.startBtn}
								onMouseEnter={() => {
									setStartButtonType('hover');
								}}
								onMouseLeave={() => {
									setStartButtonType('normal');
								}}
								onClick={(e) => {
									history.push('/main');
									setStartButtonType('clicked');
								}}
							/>
						</Grid>
					</Grid>
					<Grid className={classes.introBodyWhite}></Grid>
					<Grid className={classes.introFooter}></Grid>
				</Grid>
			</Desktop>
			<Mobile>
			<Grid className={classes.introNavWrapper}>
					<Grid className={styles.introNavText}>VOCABULARY TEST</Grid>
				</Grid>
				<Grid className={classes.introBodyWrapper}>
					<Grid className={styles.introBodyText}>
						모바일에서 시작하기 버튼을 클릭 테스트를 시작하세요.
						<p>Your are in {Portrait ? 'portrait' : 'landscape'} orientation</p>
					</Grid>
					<Grid className={classes.introBodyContentMobile}>
						<Grid className={classes.introImgWrapper}>
							<img
								src={introImg}
								alt={introImg}
								className={classes.introImg}
							/>
							<img
								src={ground}
								alt={ground}
								className={classes.introGround}
							/>
							+
							<img
								src={
									startButtonType === 'clicked'
										? startBtnClicked
										: startButtonType === 'hover'
										? startBtnHover
										: startBtn
								}
								alt={startBtn}
								className={classes.startBtn}
								onMouseEnter={() => {
									setStartButtonType('hover');
								}}
								onMouseLeave={() => {
									setStartButtonType('normal');
								}}
								onClick={(e) => {
									history.push('/main');
									setStartButtonType('clicked');
								}}
							/>
						</Grid>
					</Grid>
					<Grid className={classes.introBodyWhite}></Grid>
					<Grid className={classes.introFooter}></Grid>
				</Grid>
			</Mobile>
		</div>
	);
}

export default Intro;
