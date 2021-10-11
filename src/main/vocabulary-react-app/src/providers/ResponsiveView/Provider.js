import React, {useState, useEffect, useRef} from 'react';
import PropTypes from 'prop-types';
import {makeStyles} from '@material-ui/styles';

import Context from './Context';
import {setViewScaleValue} from './sizeRefactor';

const Provider = ({children}) => {
	const containerRef = useRef(null);
	const [options, setOptions] = useState({
		scaleToFit: true,
		enableScaleUp: true,
		scaleToFitType: 'fit',
		desiredScale: undefined,
		minimumScale: undefined,
		maximumScale: undefined,
		centerHorizontally: true,
		centerVertically: false,
		horizontalPadding: 0,
		verticalPadding: 0,
	});
	const [currentScale, setCurrentScale] = useState(1);
	const classes = makeStyles(() => ({
		root: {
			// overflowX: 'hidden',
			position: 'absolute',
			width: 2450, // 프로젝트 width **수정금지**
			maxHeight: 1200, // 프로젝트 height **수정금지**
		},
	}))();

	useEffect(() => {
		if (containerRef.current === null) return;
		// 처음 실행시, 비율조절
		resizeHandler();
		window.addEventListener('resize', resizeHandler);
		return () => {
			window.removeEventListener('resize', resizeHandler);
		};
	}, [containerRef]);

	const resizeHandler = () => {
		const desiredScale = setViewScaleValue({
			view: containerRef.current,
			...options,
		});
		setCurrentScale(desiredScale);
	};
	return (
		<Context.Provider
			value={{
				currentScale,
				options,
				setViewScaleValue,
			}}
		>
			<div ref={containerRef} className={classes.root}>
				{children}
			</div>
		</Context.Provider>
	);
};

Provider.propTypes = {
	children: PropTypes.any,
};

export default Provider;
