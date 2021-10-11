import {makeStyles} from '@material-ui/core/styles';
export default makeStyles((theme) => ({
	root: {
		'& .MuiStepLabel-iconContainer': {
			display: 'none',
		},
		'& .MuiButton-containedPrimary': {
			marginTop: 50,
		},
		'& .MuiStepLabel-label.MuiStepLabel-active': {
			color: '#fff',
			opacity: 1,
		},
		'& .MuiStepper-root': {
			backgroundColor: '#43adac !important',
		},
		'& .MuiPaper-root': {
			height: 25,
		},
		'& .MuiStepLabel-label': {
			fontFamily: 'Roboto',
			fontSize: 18,
			fontWeight: 400,
			fontStretch: 'normal',
			fontStyle: 'normal',
			lineHeight: 1.14,
			letterSpacing: -0.31,
			width: 180,
			color: '#ffffff',
			opacity: 0.6,
		},
		'& .MuiStepLabel-completed': {
			fontWeight: 600,
			opacity: 1,
		},
	},
	button: {
		marginRight: theme.spacing(1),
	},
	instructions: {
		marginTop: theme.spacing(1),
		marginBottom: theme.spacing(1),
	},
	ArrowRightIcon: {
		width: 138,
		height: 33,
		position: 'absolute',
		left: 250,
		color: '#fff',
	},
}));
