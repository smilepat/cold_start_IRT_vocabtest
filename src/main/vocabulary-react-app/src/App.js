import styles from './App.module.css';
import React, {useState, useEffect, useRef} from 'react';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom';
import ResponsiveView from './providers/ResponsiveView';
import Intro from './components/Intro/Intro';
import Main from './components/Main/Main';
import Error from './components/Error/Error';
import Result from './components/Result/Result';
import axios from "./axios/axios"

function App() {
	const [examId, setExamId] = useState(0);
	const [examData, setExamData] = useState(null);
	const [openQuit, setOpenQuit] = useState(false);
	const [openFinish, setOpenFinish] = useState(false);
	const [noInput, setNoInput] = useState(false);
	const completed = useRef();

	// Start IRT CAT exam
	useEffect(() => {
		async function startIrtExam() {
			try {
				const result = await axios.post(`/api/irt/exam/start`, {});
				if (!completed.current) {
					const exam = result.data;
					setExamId(exam.wordExamSeqno);
					setExamData(exam);
				}
				console.log('IRT CAT exam started:', result.data);
			} catch (error) {
				console.error('Failed to start exam:', error);
			}
		}
		startIrtExam();
		return () => {
			completed.current = true;
		};
	}, []);

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
									examId={examId}
									seqNo={examId}
									examData={examData}
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
							render={() => <Result examId={examId} seqNo={examId} />}
						/>
						<Route path='*' component={Error} />
					</Switch>
				</Router>
			</ResponsiveView>
		</div>
	);
}

export default App;
