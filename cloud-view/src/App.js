import React, { useState, useEffect } from 'react';
import { Container, Switch } from "@mui/material";
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import moment from 'moment';

const unixToMilli = unix => unix * 1000;

const initBaseOptions = (title, yName, yUnit, yMin, yMax, yData) => {
    return ({
        chart: {
            zoomType: 'xy',
            height: '350px',
            animation: true,
        },
        title: {
            text: title
        },
        xAxis: {
            type: 'datetime',
            verticalAlign: 'middle',
        },
        plotOptions: {
            series: {
                turboThreshold: 3000
            }
        },
        time: {
            timezone: 'Asia/Taipei',
            useUTC: false,
        },
        yAxis: [{
            labels: {
                format: `{value} ${yUnit}`
            },
            title: {
                text: yName
            },
            min: yMin,
            max: yMax,
        }],
        legend: {
            enabled: true
        },
        series: [{
            name: yName,
            type: 'line',
            data: yData,
        }]
    });
}

const initFixDataOptions = () => {
    const totalMinutes = (60 * 24) + 1;

    let fix = [];

    for (let i = 0; i < totalMinutes; i++) {
        const date = moment().startOf('day').add(i, 'minutes');

        fix.push({
            x: unixToMilli(date.unix()),
            y: ((i % 3) - 1)
        });
    }

    return initBaseOptions('Fix State', 'State', '', -1, 1, fix);
};

const initMeterDataOptions = () => {
    const totalMinutes = (60 * 24) + 1;

    let power = [];

    for (let i = 0; i < totalMinutes; i++) {
        const date = moment().startOf('day').add(i, 'minutes');

        power.push({
            x: unixToMilli(date.unix()),
            y: i
        });
    }

    return initBaseOptions('Meter Data', 'Active Power', 'kW', -100, 2000, power);
}

function App() {
    const now = moment();

    const [auto, setAuto] = useState(true);
    const updateAuto = event => setAuto(event.target.checked);

    const [meterDataOptions, setMeterDataOptions] = useState({});
    const [fixDataOptions, setFixDataOptions] = useState({});

    useEffect(() => {
        const meterData = initMeterDataOptions();
        const fixData = initFixDataOptions();

        setMeterDataOptions(meterData);
        setFixDataOptions(fixData);
    }, []);

    return (
        <Container maxWidth="xl">
            <h1>Electric Data of {now.format("YYYY-MM-DD")}</h1>
            <Switch checked={auto} onChange={updateAuto} />
            <HighchartsReact
                highcharts={Highcharts}
                options={meterDataOptions}
            />

            <HighchartsReact
                highcharts={Highcharts}
                options={fixDataOptions}
            />
        </Container>
    );
}

export default App;
