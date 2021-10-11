import {makeStyles} from '@material-ui/core/styles';
export default makeStyles((theme) => ({
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
	h2Text: {fontFamily: 'NotoSans', fontSize: '3rem', textAlign: 'center'},
	modalImgWrapper: {
		marginTop: '-4rem',
		display: 'flex',
		justifyContent: 'center',
		alignItems: 'center',
	},
	pText: {
		marginTop: '4rem',
		fontFamily: 'NotoSans',
		fontSize: '2rem',
		textAlign: 'center',
	},
	closeButton: {
		marginTop: '2rem',
		backgroundColor: '#68c8c7',
		color: 'white',
		width: '30rem',
		height: '6rem',
		borderRadius: '2.2rem',
		fontSize: '2.3rem',
	},
}));
