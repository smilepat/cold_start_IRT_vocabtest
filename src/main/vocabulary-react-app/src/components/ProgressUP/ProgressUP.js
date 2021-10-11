import React, {useEffect} from 'react';
import Stepper from '@material-ui/core/Stepper';
import Step from '@material-ui/core/Step';
import StepLabel from '@material-ui/core/StepLabel';
import ArrowRightIcon from '@material-ui/icons/ArrowRight';

import useStyles from './style';
function getSteps() {
	return ['Stage 1', 'Stage 2'];
}
export default function ProgressUp({counter}) {
	const classes = useStyles();
	const [activeStep, setActiveStep] = React.useState(0);
	const [skipped, setSkipped] = React.useState(new Set());
	const steps = getSteps();

	const isStepSkipped = (step) => {
		return skipped.has(step);
	};

	const handleNext = () => {
		let newSkipped = skipped;
		if (isStepSkipped(activeStep)) {
			newSkipped = new Set(newSkipped.values());
			newSkipped.delete(activeStep);
		}

		setActiveStep((prevActiveStep) => prevActiveStep + 1);
		setSkipped(newSkipped);
	};
	useEffect(() => {
		if (counter === 1 || counter === 5) {
			handleNext();
		}
	}, [counter]);

	return (
		<div className={classes.root}>
			<Stepper activeStep={activeStep}>
				<ArrowRightIcon className={classes.ArrowRightIcon} />
				{steps.map((label, index) => {
					const stepProps = {};
					const labelProps = {};
					if (isStepSkipped(index)) {
						stepProps.completed = false;
					}
					return (
						<Step key={label} {...stepProps}>
							<StepLabel {...labelProps}>{label}</StepLabel>
						</Step>
					);
				})}
			</Stepper>
		</div>
	);
}
