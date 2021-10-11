import React, {useState} from 'react';
import styles from './style.mudule.css';
import ArrowRightIcon from '@material-ui/icons/ArrowRight';
import {makeStyles} from '@material-ui/core';

import computerLearningActive from 'Images/LearningSteps/step-cavt-a.png';
import aiBasedLearningActive from 'Images/LearningSteps/step-abvl-a.png';
import aiBasedReadingActive from 'Images/LearningSteps/step-absr-a.png';
import computerLearningNonActive from 'Images/LearningSteps/step-cavt-na.png';
import aiBasedLearningNonActive from 'Images/LearningSteps/step-abvl-na.png';
import aiBasedReadingNonActive from 'Images/LearningSteps/step-absr-na.png';

const useStyles = makeStyles(() => ({
	ArrowRightIcon: {
		fontSize: 60,
		color: 'white',
	},
}));
function Steps() {
	const [id, setId] = useState(1);
	const handleToggle = (value) => {
		setId(value);
	};
	const classes = useStyles();
	return (
		<div className='steps'>
			<div
				className=''
				onClick={() => {
					handleToggle(1);
				}}
			>
				<img
					src={
						id === 1
							? computerLearningActive
							: computerLearningNonActive
					}
					alt={computerLearningActive}
					className={styles.learningDiv}
					style={{width: 387}}
				/>
			</div>
			<div>
				<ArrowRightIcon className={classes.ArrowRightIcon} />
			</div>
			<div
				className=''
				onClick={() => {
					handleToggle(2);
				}}
			>
				<img
					src={
						id === 2
							? aiBasedLearningActive
							: aiBasedLearningNonActive
					}
					alt={aiBasedLearningNonActive}
					className={styles.learningDiv}
					style={{width: 387}}
				/>
			</div>
			<div>
				<ArrowRightIcon className={classes.ArrowRightIcon} />
			</div>
			<div
				className=''
				onClick={() => {
					handleToggle(3);
				}}
			>
				<img
					src={
						id === 3
							? aiBasedReadingActive
							: aiBasedReadingNonActive
					}
					alt={aiBasedReadingNonActive}
					className={styles.learningDiv}
					style={{width: 387}}
				/>
			</div>
		</div>
	);
}

export default Steps;
