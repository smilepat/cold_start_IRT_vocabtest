import React from 'react';
import {useHistory} from 'react-router-dom';
import RubberBand from 'react-reveal/RubberBand';

import Fade from '@material-ui/core/Fade';
import Modal from '@material-ui/core/Modal';
import Button from '@material-ui/core/Button';
import Backdrop from '@material-ui/core/Backdrop';
import DialogActions from '@material-ui/core/DialogActions';

import useStyles from './styles';

import modalImg2 from 'Images/Modal/analysis.png';

function FinishModal({open1, onClickFinish, handleClose1}) {
	const classes = useStyles();
	const history = useHistory();

	return (
		<div>
			<Modal
				className={classes.modal}
				open={open1}
				onClose={handleClose1}
				closeAfterTransition
				BackdropComponent={Backdrop}
				BackdropProps={{
					classes: {
						root: classes.backrop,
					},
				}}
			>
				<Fade in={open1}>
					<div className={classes.paper}>
						<h2
							id='transition-modal-title'
							className={classes.h2Text}
						>
							테스트 완료!
						</h2>
						<RubberBand>
							<div className={classes.modalImgWrapper}>
								<img src={modalImg2} alt={modalImg2} />
							</div>
						</RubberBand>
						<p
							id='transition-modal-description'
							className={classes.pText}
						>
							단어 테스트 결과를 분석중입니다.
						</p>
						<DialogActions style={{justifyContent: 'center'}}>
							<Button
								variant='contained'
								className={classes.closeButton}
								onClick={() => {
									handleClose1();
									history.push('/result');
								}}
							>
								결과 보기
							</Button>
						</DialogActions>
					</div>
				</Fade>
			</Modal>
		</div>
	);
}

export default FinishModal;
