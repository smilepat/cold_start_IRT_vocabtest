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

function InputModal({input1, onClickInput, handleInput}) {
	const classes = useStyles();
	const history = useHistory();

	return (
		<div>
			<Modal
				className={classes.modal}
				open={input1}
				onClose={handleInput}
				closeAfterTransition
				BackdropComponent={Backdrop}
				BackdropProps={{
					classes: {
						root: classes.backrop,
					},
				}}
			>
				<Fade in={input1}>
					<div className={classes.paper}>
						<h2
							id='transition-modal-title'
							className={classes.h2Text}
						>
						   꼭 하나는 선택해야합니다!
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
							
						</p>
						<DialogActions style={{justifyContent: 'center'}}>
							<Button
								variant='contained'
								className={classes.closeButton}
								onClick={() => {
									handleInput();
								}}
							>
								닫기
							</Button>
						</DialogActions>
					</div>
				</Fade>
			</Modal>
		</div>
	);
}

export default InputModal;
