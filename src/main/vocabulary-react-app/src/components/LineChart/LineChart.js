import React from 'react';
import {AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip} from 'recharts';

import styles from './styles.module.css';

const LineChart = ({examResult, level}) => {
	const answers = examResult.map((result) => {
		return result.correctYn;
	});
	const lowLimits = examResult.map((result) => {
		return result.wordSeqnoLowLimit;
	});
	const highLimits = examResult.map((result) => {
		return result.wordSeqnoHighLimit;
	});
	const graphData = [
		{
			name: '',
			uv: 0,
		},
	];

	//console.log("Chart")
	let lastMedian = 0
	answers.forEach((answer, index) => {
		let median = Math.floor( (highLimits[index] + lowLimits[index]) / 2 );
		//console.log("Index: " + index)
		//console.log("Median: " + median)

		let order =
			index === 0 ? 'st' : index === 1 ? 'nd' : index === 2 ? 'rd' : 'th';

		if (answer != null) {
			if (lastMedian != median) {
				graphData.push({
					name: `${index + 1}${order}`,
					uv: median,
				});

				lastMedian = median
			}
		}
		/*
		if (answer === 'Y') {
			graphData.push({
				name: `${index + 1}${order}`,
				uv: level,
			});
			//console.log("True: " + answer + " - " + level)
		} else {
			graphData.push({
				name: `${index + 1}${order}`,
				uv: -level,
			});
			//console.log("Wrong: " + answer + " - " + level)
		}
		*/
	});

	//let x = level <= 4 ? 4 : 8;
	let x = level;
	return (
		<AreaChart
			className={styles.barChart}
			width={500}
			height={687}
			data={graphData}
			margin={{
				top: 10,
				right: 30,
				left: 0,
				bottom: 0,
			}}
		>
			<CartesianGrid strokearray='7 7' stroke='#afafaf' />
			<XAxis dataKey='name' tick={{fontSize: 16, fill: '#3C9C9B'}} />
			<YAxis
				tick={{fontSize: 16, fill: '#3C9C9B'}}
				type='number'
				domain={[(-x: x)]}
				dataKey='uv'
			/>
			<Tooltip />

			<defs>
				<linearGradient id='splitColor' x1='0' y1='0' x2='0' y2='1'>
					<stop offset={'0%'} stopColor='#56B4D3' stopOpacity={1} />
					<stop offset={'100%'} stopColor='#9A3C5E' stopOpacity={1} />
				</linearGradient>
			</defs>
			<Area
				type='monotone'
				dataKey='uv'
				stroke='#A9E3F8'
				strokeWidth={4}
				fill='url(#splitColor)'
			/>
		</AreaChart>
	);
};

export default LineChart;
