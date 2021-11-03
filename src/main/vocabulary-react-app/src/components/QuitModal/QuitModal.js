import React, {useState} from 'react';
import Jump from 'react-reveal/Jump';
import {useHistory} from 'react-router-dom';

import Fade from '@material-ui/core/Fade';
import Modal from '@material-ui/core/Modal';
import Backdrop from '@material-ui/core/Backdrop';
import DialogActions from '@material-ui/core/DialogActions';

import useStyles from './styles';

import modalImg from 'Images/Modal/question_img.png';
import doneBtn from 'Images/Modal/exit-btn-n.png';
import doneBtnHover from 'Images/Modal/exit-btn-h.png';
import doneBtnClicked from 'Images/Modal/exit-btn-p.png';
import returnBtn from 'Images/Modal/return-btn-n.png';
import returnBtnHover from 'Images/Modal/return-btn-h.png';
import returnBtnClicked from 'Images/Modal/return-btn-p.png';

function QuitModal({open, onClickQuit, handleClose}) {
	const classes = useStyles();
	const history = useHistory();
	const [doneButton, setDoneButton] = useState('deafult');
	const [returnButton, setReturnButton] = useState('deafult');
	return (
		<div>
			<Modal
				className={classes.modal}
				open={open}
				onClose={handleClose}
				closeAfterTransition
				BackdropComponent={Backdrop}
				BackdropProps={{
					classes: {
						root: classes.backrop,
					},
				}}
			>
				<Fade in={open}>
					<div className={classes.paper}>
						<h2
							id='transition-modal-title'
							className={classes.h2Text}
						>
							테스트를 종료할까요????
						</h2>
						<Jump>
							<div className={classes.modalImgWrapper}>
								<img src={modalImg} alt={modalImg} />
							</div>
						</Jump>
						<p
							id='transition-modal-description'
							className={classes.pText}
						>
							테스트를 종료하면 <br /> 지금까지 입력한 답안이
							사라집니다.
						</p>
						<DialogActions style={{justifyContent: 'center'}}>
							<img
								src={
									doneButton === 'clicked'
										? doneBtnClicked
										: doneButton === 'hover'
										? doneBtnHover
										: doneBtn
								}
								alt={doneBtn}
								variant='contained'
								className={classes.doneBtn}
								onMouseEnter={() => {
									setDoneButton('hover');
								}}
								onMouseLeave={() => {
									setDoneButton('default');
								}}
								onClick={() => {
									handleClose();
									history.push('/');
									setDoneButton('clicked');
								}}
							/>
							<img
								src={
									returnButton === 'clicked'
										? returnBtnClicked
										: returnButton === 'hover'
										? returnBtnHover
										: returnBtn
								}
								alt={returnBtn}
								variant='outlined'
								onMouseEnter={() => {
									setReturnButton('hover');
								}}
								onMouseLeave={() => {
									setReturnButton('default');
								}}
								className={classes.returnBtn}
								onClick={() => {
									handleClose();
									setReturnButton('clicked');
								}}
							/>
						</DialogActions>
					</div>
				</Fade>
			</Modal>
		</div>
	);
}

export default QuitModal;
