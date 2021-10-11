import React, {useEffect, useRef} from 'react';
import ArrowRightIcon from '@material-ui/icons/ArrowRight';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import {Grid} from '@material-ui/core';
import ProgressUp from '../ProgressUP/ProgressUP';

import useStyles from './styles';
import styles from './style.module.css';

export default function ProgressContainer({
	done,
	counter,
	getSteps,
	activeStep,
	isStepSkipped,
}) {
	const classes = useStyles();
	const steps = getSteps;
	const stepperRef = useRef();
	console.log('active step', activeStep);
	useEffect(() => {
		if (counter > 4) stepperRef.current.scrollLeft += 100;
	}, [counter]);

	return (
		<Grid className={classes.root}>
			<Grid className={styles.testTitle}>Test In Progress</Grid>
			<Grid className={styles.progressWrapper}>
				<Grid className={styles.general}>
					<div className={styles.generalText}>General</div>
					<div className={styles.breakGeneral}>|</div>
					<div className={styles.generalStages}>
						<ProgressUp counter={counter} />
					</div>
				</Grid>
				<Grid className={styles.specific}>
					<Grid className={styles.specificText}>Specific</Grid>
					<div className={styles.breakSpecific}>|</div>
					<Grid className={styles.downProgress} ref={stepperRef}>
						<Stepper activeStep={activeStep}>
							{steps.map((label, index) => {
								const stepProps = {};
								const labelProps = {};
								if (isStepSkipped(index)) {
									stepProps.completed = false;
								}
								return (
									<Step key={label} {...stepProps}>
										<StepLabel {...labelProps}>
											{counter - 1 === index + 1 &&
											index < activeStep ? (
												<ArrowRightIcon
													className={
														classes.arrowRightIcon
													}
												/>
											) : (
												<ArrowRightIcon
													className={
														classes.arrowRightIconBlurred
													}
												/>
											)}
										</StepLabel>
									</Step>
								);
							})}
						</Stepper>
					</Grid>
				</Grid>
			</Grid>
		</Grid>
	);
}
