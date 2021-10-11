import {makeStyles} from '@material-ui/core/styles';
export default makeStyles((theme) => ({
	root: {
		position: 'relative',
		left: '26%',
		width: 520,
		height: 40,
		'& .MuiPaper-root': {
			backgroundColor: '#F9FAFF',
		},
		'& .MuiStepper-root': {
			padding: 0,
		},
		'& .MuiStepConnector-root': {
			display: 'none',
		},
		'& MuiStepLabel-label.MuiStepLabel-active': {
			position: 'relative',
			right: '18px',
			color: '#fff',
		},
		'& .MuiStepIcon-root': {
			color: '#43adac',
			opacity: 0.4,
			width: 23,
			height: 22,
		},
		'& .MuiStepIcon-root.MuiStepIcon-active': {
			color: '#43adac',
			opacity: 1,
		},
		'& .MuiStepIcon-root.MuiStepIcon-completed': {
			position: 'relative',
			color: '#43adac',
			opacity: 1,
		},
		'& .MuiStepIcon-text': {
			fontSize: 14,
			fill: '#fff',
			fontWeight: 500,
			color: '#fff',
		},
	},
	button: {
		marginRight: theme.spacing(1),
	},
	instructions: {
		marginTop: theme.spacing(1),
		marginBottom: theme.spacing(1),
	},
	arrowRightIcon: {width: 40, height: 25, color: '#43adac'},
	arrowRightIconBlurred: {
		width: 40,
		height: 25,
		color: '#43adac',
		opacity: 0.5,
	},
}));
