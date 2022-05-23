import React, {useState, useEffect} from 'react';
import {Container, Switch} from "@mui/material";
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import moment from 'moment';

const unixToMilli = unix => unix * 1000;

const initOptions = () => {
    const totalMinutes = (60 * 24) + 1;

    let power = [];
    let fix = [];

    for (let i = 0; i < totalMinutes; i++) {
        const date = moment().startOf('day').add(i, 'minutes');

        power.push({
            x: unixToMilli(date.unix()),
            y: 0
        });
        fix.push({
            x: unixToMilli(date.unix()),
            y: null
        });
    }

    return ({
        title: {
            text: 'Electric Data'
        },
        xAxis: {
            type: 'datetime'
        },
        yAxis: [{
            labels: {
                format: '{value} kW'
            },
            title: {
                text: 'ActivePower'
            }
        }, {
            title: {
                text: 'FixState'
            },
            opposite: true
        }],
        legend: {
            enabled: true
        },
        series: [{
            name: 'ActivePower',
            type: 'spline',
            yAxis: 1,
            data: power,
        }, {
            name: 'FixState',
            type: 'spline',
            data: fix,
        }]
    });
}

function App() {
    const now = moment();

    const [auto, setAuto] = useState(true);
    const updateAuto = event => setAuto(event.target.checked);

    const [options, setOptions] = useState(initOptions());

    useEffect(() => {
    }, []);

    console.log(options);

    return (
        <Container maxWidth="xl">
            <h1>Electric Data of {now.format("YYYY-MM-DD")}</h1>
            <Switch checked={auto} onChange={updateAuto}/>
            <HighchartsReact
                highcharts={Highcharts}
                options={options}
            />
        </Container>
    );
}

export default App;
