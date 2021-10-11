import {makeStyles} from '@material-ui/core/styles';

export default makeStyles((theme) => ({
	introWrapper: {
		width: '100%',
		height: '100%',
		backgroundColor: '#ffffff',
	},
	introNavWrapper: {
		width: '100%',
		height: '12rem',
		borderBottom: '3px solid rgba(0, 0, 0, 0.16)',
		backgroundColor: '#68c8c7',
		boxShadow: '0 3px 6px 0 rgba(0, 0, 0, 0.16)',
	},
	introBodyWrapper: {
		width: '100%',
		height: '52rem',
		backgroundColor: '#68c8c7',
	},
	introBodyContent: {
		position: 'relative',
		left: '31.2rem',
		top: '15.7rem',
		width: '143rem',
		height: '68.5rem',
		borderRadius: '1.6rem',
		boxShadow: '0.4rem 0.3rem 1.2rem 0 rgba(24, 93, 88, 0.3)',
		backgroundColor: '#f9faff',
		transform: 'translate(15%, -5%)',
	},
	introImg: {
		position: 'relative',
		top: '12rem',
		left: '35rem',
		zIndex: 1,
	},
	introGround: {
		position: 'relative',
		top: '15rem',
		left: '7rem',
	},
	startBtn: {
		position: 'relative',
		top: '33rem',
		left: '-27rem',
		cursor: 'pointer',
	},
	introBodyWhite: {
		height: '10.2rem',
		backgroundColor: 'white',
	},
	introFooter: {
		width: '100%',
		height: '10rem',
		position: 'relative',
		backgroundColor: '#68c8c7',
		top: '15.1rem',
	},
}));
