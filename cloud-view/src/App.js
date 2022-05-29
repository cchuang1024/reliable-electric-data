import React, {useEffect, useState} from 'react';
import {Alert, Collapse, Container, IconButton, Stack, TextField} from "@mui/material";
import {AdapterMoment} from '@mui/x-date-pickers/AdapterMoment';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {DatePicker} from '@mui/x-date-pickers/DatePicker';
import CloseIcon from '@mui/icons-material/Close';
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import {Timer, updateTimer} from "./timer";
import {BASE_URL} from "./config";

const unixToMilli = unix => unix * 1000;
const milliToMin = milli => milli / (1000 * 60);
const transState = state => {
    switch (state) {
        case 'INIT':
            return 0;
        case 'WAIT':
            return -1;
        case 'DONE':
        default:
            return 1;
    }
}

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
            type: 'spline',
            data: yData,
        }]
    });
}

const initFixDataOptions = () => {
    const fix = initBaseData(0);
    return initBaseOptions('Fix State', 'State', '', -1, 1, fix);
};

const initMeterDataOptions = () => {
    const power = initBaseData(0);
    return initBaseOptions('Meter Data', 'Active Power', 'kW', -100, 2000, power);
}

const initBaseData = init => {
    const totalMinutes = (60 * 24) + 1;

    let data = [];

    for (let i = 0; i < totalMinutes; i++) {
        const date = moment().startOf('day').add(i, 'minutes');

        data.push({
            x: unixToMilli(date.unix()),
            y: init
        });
    }

    return data;
};

const buildRealData = (data, start, key, init, trans) => {
    const result = initBaseData(init);
    const startMilli = unixToMilli(start.unix());

    if (_.isNil(data)) {
        return result;
    }

    data.forEach(item => {
        const timestamp = item['timestamp'];
        const value = item[key];
        const diffMin = milliToMin(timestamp - startMilli);
        result[diffMin] = {
            x: timestamp,
            y: trans.apply(null, [value])
        };
    });

    return result;
};

const buildMeterDataOptions = (dateStart, meterData) => {
    const optionData = buildRealData(meterData, dateStart, 'power', 0, meter => meter / 100.0);
    return initBaseOptions('Meter Data', 'Active Power', 'kW', -100, 2000, optionData);
};

const buildFixDataOptions = (dateStart, fixData) => {
    const optionData = buildRealData(fixData, dateStart, 'state', null, transState);
    return initBaseOptions('Fix State', 'State', '', -1, 1, optionData);
};

const dataFetcher = (dataDate, setDisplayData, setAlertMsg) => {
    const dateStart = dataDate.startOf('day');

    axios.get(`${BASE_URL}/electricData`, {
        params: {
            date: dateStart.format('YYYYMMDD')
        }
    })
        .then(resp => {
            // console.log('response: ', resp);

            const {meterData, fixData} = resp.data;
            const meterDataDisplay = buildMeterDataOptions(dateStart, meterData);
            const fixDataDisplay = buildFixDataOptions(dateStart, fixData);

            // console.log('meter data: ', meterDataDisplay);

            setDisplayData({
                meterData: meterDataDisplay,
                fixData: fixDataDisplay
            });
        }).catch(ex => {
        console.error('error: ', ex);
        setAlertMsg(ex.message);
    });
};

function App() {
    const now = moment();

    const initMeterDataDisplay = initMeterDataOptions();
    const initFixDataDisplay = initFixDataOptions();

    const [displayData, setDisplayData] = useState({
        meterData: initMeterDataDisplay,
        fixData: initFixDataDisplay
    });
    const [dataDate, setDataDate] = useState(now);
    const [alertMsg, setAlertMsg] = useState('');

    const fetchDataByDate = date => dataFetcher(date, setDisplayData, setAlertMsg);

    const updater = _.partial(dataFetcher, now, setDisplayData, setAlertMsg);
    const clearTimer = () => Timer.clearAll();
    const autoUpdate = () => updateTimer("electricData", "0", updater);
    const isSameDate = (moment1, moment2) => moment1.startOf('day').unix() === moment2.startOf('day').unix();

    useEffect(() => {
        clearTimer();

        // console.log("fetch data by date: ", now.format("YYYYMMDD"));
        fetchDataByDate(now);

        autoUpdate()
    }, []);

    useEffect(() => {
        clearTimer();

        // console.log("fetch data by date: ", dataDate.format("YYYYMMDD"));
        fetchDataByDate(dataDate);

        if (isSameDate(now, dataDate)) {
            autoUpdate();
        }
    }, [dataDate]);

    return (
        <Container maxWidth="xl">
            <Stack sx={{width: '100%'}} spacing={2}>
                <Collapse in={!_.isEmpty(alertMsg.split(''))}>
                    <Alert severity="error"
                           action={
                               <IconButton
                                   aria-label="close"
                                   color="inherit"
                                   size="small"
                                   onClick={() => {
                                       setAlertMsg('');
                                   }}
                               >
                                   <CloseIcon fontSize="inherit"/>
                               </IconButton>
                           }
                           sx={{mb: 2}}>
                        {alertMsg}
                    </Alert>
                </Collapse>
            </Stack>

            <h1>Electric Data of {dataDate.format("YYYY-MM-DD")}</h1>

            <Stack direction="row" spacing={2}>
                <LocalizationProvider dateAdapter={AdapterMoment}>
                    <DatePicker
                        label="Date"
                        value={dataDate}
                        onChange={(newDate) => {
                            setDataDate(newDate);
                        }}
                        renderInput={(params) => <TextField {...params} />}
                    />
                </LocalizationProvider>
            </Stack>

            <Stack>
                <HighchartsReact
                    highcharts={Highcharts}
                    options={displayData.meterData}
                />

                <HighchartsReact
                    highcharts={Highcharts}
                    options={displayData.fixData}
                />
            </Stack>
        </Container>
    );
}

export default App;
