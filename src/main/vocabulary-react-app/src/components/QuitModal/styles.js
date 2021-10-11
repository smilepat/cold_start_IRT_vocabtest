import {makeStyles} from '@material-ui/core/styles';
export default makeStyles(() => ({
	modal: {
		display: 'flex',
		alignItems: 'center',
		justifyContent: 'center',
	},
	paper: {
		marginTop: '-20rem',
		color: 'white',
		minHeight: '60vh',
		maxHeight: '60vh',
		minWidth: '30vw',
		maxWidth: '30vw',
		outline: 'none',
	},
	backDrop: {
		background: 'rgba(36, 36, 36, 0.85)',
	},
	h2Text: {
		fontFamily: 'NotoSans',
		fontSize: '3rem',
		textAlign: 'center',
	},
	modalImgWrapper: {
		display: 'flex',
		justifyContent: 'center',
		alignItems: 'center',
	},
	pText: {fontFamily: 'NotoSans', fontSize: '2rem', textAlign: 'center'},
	doneBtn: {
		marginTop: '2rem',
		width: '17rem',
		height: '8rem',
	},
	returnBtn: {
		marginTop: '2rem',
		width: '17rem',
		height: '8rem',
	},
}));
