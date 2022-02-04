import styles from './App.module.css';
import React, {useState, useEffect, useRef} from 'react';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import ResponsiveView from './providers/ResponsiveView';
import Intro from './components/Intro/Intro';
import Main from './components/Main/Main';
import Error from './components/Error/Error';
import Result from './components/Result/Result';
import WordCard from './components/WordCard/WordCard';
import axios from "axios/axios"

function App() {
	const [seqNo, setSeqNo] = useState(0);
	const [openQuit, setOpenQuit] = useState(false);
	const [openFinish, setOpenFinish] = useState(false);
	const [noInput, setNoInput] = useState(false);
	const completed = useRef();

	useEffect(() => {
		async function testStart() {
			try {
				const result = await axios.post(`/api/word-exams`, {});
				if (!completed.current) {
					setSeqNo(result.data.data.wordExamSeqno);
				}
				console.log('result', result);
			} catch (error) {
				console.log(error.response);
			}
		}
		testStart();
		return () => {
			completed.current = true;
		};
	}, []);

	console.log(seqNo);
	return (
		<div className={styles.App}>
			<ResponsiveView>
				<Router>
					<Switch>
						<Route path='/' exact component={Intro} />
						<Route
							path='/main'
							render={() => (
								<Main
									seqNo={seqNo}
									onClickQuit={() => setOpenQuit(true)}
									onClickFinish={() => setOpenFinish(true)}
									onClickInput={() => setNoInput(true)}
									open={openQuit}
									handleClose={() => setOpenQuit(false)}
									open1={openFinish}
									input1={noInput}
									handleClose1={() => setOpenFinish(false)}
									handleInput={() => setNoInput(false)}
								/>
							)}
						/>
						<Route
							path='/result'
							render={() => <Result seqNo={seqNo} />}
						/>
						<Route
							path='/wordcard'
							render={() => (
								<WordCard
									seqNo={seqNo}
									onClickQuit={() => setOpenQuit(true)}
									open={openQuit}
									handleClose={() => setOpenQuit(false)}
								/>
							)}
						/>
						<Route path='*' component={Error} />
					</Switch>
				</Router>
			</ResponsiveView>
		</div>
	);
}

export default App;
