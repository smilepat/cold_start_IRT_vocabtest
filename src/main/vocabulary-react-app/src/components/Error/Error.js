import React from 'react';
import {Link as RouterLink} from 'react-router-dom';

import Button from '@material-ui/core/Button';

import useStyles from './style';

const ErrorPage = () => {
	const classes = useStyles();
	return (
		<>
			<h2 className={classes.h2Text}>404 NOT FOUND</h2>
			<p className={classes.pText}>잘못된 페이지입니다.</p>
			<Button
				variant='contained'
				color='primary'
				size='small'
				className={classes.button}
				component={RouterLink}
				to='/'
			>
				홈으로
			</Button>
		</>
	);
};

export default ErrorPage;
