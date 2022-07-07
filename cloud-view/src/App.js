import React, {useEffect, useState} from 'react';
import {
    Alert,
    Box,
    Collapse,
    Container,
    IconButton,
    Paper,
    Stack,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField
} from "@mui/material";
import {DataGrid} from "@mui/x-data-grid";
import {AdapterMoment} from '@mui/x-date-pickers/AdapterMoment';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {DatePicker} from '@mui/x-date-pickers/DatePicker';
import CloseIcon from '@mui/icons-material/Close';
import Highcharts from 'highcharts';
import HighchartsReact from 'highcharts-react-official';
import moment from 'moment';
import axios from 'axios';
import _ from 'lodash';
import * as stat from 'simple-statistics'
import moize from 'moize';
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

const buildFixDataGrid = (fixData) => fixData.map(data => ({
    id: data.timestamp,
    timestamp: moment(data.timestamp).format('YYYY/MM/DD HH:mm:ss'),
    state: data.state,
    initTime: moment(data.initTime).format("YYYY/MM/DD HH:mm:ss"),
    fixTime: moment(data.fixTime).format("YYYY/MM/DD HH:mm:ss"),
    doneTime: moment(data.doneTime).format("YYYY/MM/DD HH:mm:ss"),
}));

const GRID_COL_DEF = [
    {field: 'id', headerName: '時戳', width: 130},
    {field: 'timestamp', headerName: '資料時間', width: 250},
    {field: 'state', headerName: '資料狀態', width: 150},
    {field: 'initTime', headerName: '初始化時間', width: 250},
    {field: 'fixTime', headerName: '補值時間', width: 250},
    {field: 'doneTime', headerName: '完成時間', width: 250},
];

const STAT_CAPTION_LIST = [
    {key: 'totalMeter', name: '總電表筆數'},
    {key: 'totalFix', name: '總補值筆數'},
    {key: 'avgFixLatency', name: '平均補值延遲'},
    {key: 'minFixLatency', name: '最短補值延遲'},
    {key: 'maxFixLatency', name: '最長補值延遲'}
];

const createStatDataWithUnit = (key, caption, value, unit) => ({key, caption, value, unit});

const createStatData = (key, caption, value) => createStatDataWithUnit(key, caption, value, '');

const initStatData = () => STAT_CAPTION_LIST.map(cap => createStatData(cap.key, cap.name, 0));

const dataSize = (data) => (_.isEmpty(data) || _.isNil(data)) ? 0 : data.length;

const calTotalMeter = (meterData, fixData) => {
    const caption = STAT_CAPTION_LIST.find(cap => cap.key === 'totalMeter');
    return createStatDataWithUnit(caption.key, caption.name, dataSize(meterData), '筆');
};

const calTotalFix = (meterData, fixData) => {
    const caption = STAT_CAPTION_LIST.find(cap => cap.key === 'totalFix');
    return createStatDataWithUnit(caption.key, caption.name, dataSize(fixData), '筆');
};

const buildFixLatency = (fixData) => {
    if (_.isNil(fixData) || _.isEmpty(fixData)) {
        return [0];
    }

    return fixData.map(fix => {
        const {timestamp, doneTime, state} = fix;
        if (state !== 'DONE') {
            return 0;
        } else {
            return doneTime - timestamp;
        }
    })
};

const memBuildFixLatency = moize(buildFixLatency);

const UNIT_MILLI = "毫秒";
const UNIT_SECOND = "秒";
const UNIT_MINUTE = "分鐘";
const UNIT_HOUR = "小時";

const FIX_FLOAT = 2;
const toFixedFloat = (num) => Number.parseFloat(num).toFixed(FIX_FLOAT);

const milliToReadable = (milli) => {
    if (milli < 1500) {
        return [toFixedFloat(milli), UNIT_MILLI];
    }

    const second = milli / 1000.0;

    if (second < 90) {
        return [toFixedFloat(second), UNIT_SECOND];
    }

    const minute = second / 60.0;

    if (minute < 90) {
        return [toFixedFloat(minute), UNIT_MINUTE];
    }

    const hour = minute / 60;

    return [toFixedFloat(hour), UNIT_HOUR];
};

const milliToReadableRaw = (milli) => {
    return [toFixedFloat(milli), UNIT_MILLI];
};

const calAvgFixLatency = (meterData, fixData) => {
    const caption = STAT_CAPTION_LIST.find(cap => cap.key === 'avgFixLatency');
    const fixLatency = memBuildFixLatency(fixData);
    const avgMilli = stat.mean(fixLatency);
    const [value, unit] = milliToReadable(avgMilli);

    return createStatDataWithUnit(caption.key, caption.name, value, unit);
};

const calMinFixLatency = (meterData, fixData) => {
    const caption = STAT_CAPTION_LIST.find(cap => cap.key === 'minFixLatency');
    const fixLatency = memBuildFixLatency(fixData);
    const min = stat.min(fixLatency);
    const [value, unit] = milliToReadable(min)

    return createStatDataWithUnit(caption.key, caption.name, value, unit);
};

const calMaxFixLatency = (meterData, fixData) => {
    const caption = STAT_CAPTION_LIST.find(cap => cap.key === 'maxFixLatency');
    const fixLatency = memBuildFixLatency(fixData);
    const max = stat.max(fixLatency);
    const [value, unit] = milliToReadable(max);

    return createStatDataWithUnit(caption.key, caption.name, value, unit);
};

const STAT_FUNC_LIST = [
    calTotalMeter,
    calTotalFix,
    calAvgFixLatency,
    calMinFixLatency,
    calMaxFixLatency
];

const buildStatData = (meterData, fixData) => {
    return STAT_FUNC_LIST.map(
        (func) => func(meterData, fixData));
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
            // const fixDataDisplay = buildFixDataOptions(dateStart, fixData);
            const fixDataDisplay = buildFixDataGrid(fixData);
            const statDataDisplay = buildStatData(meterData, fixData);

            // console.log('stat data: ', statDataDisplay);
            // console.log('meter data: ', meterDataDisplay);

            setDisplayData({
                meterData: meterDataDisplay,
                fixData: fixDataDisplay,
                statData: statDataDisplay
            });
        }).catch(ex => {
        console.error('error: ', ex);
        setAlertMsg(ex.message);
    });
};

function App() {
    const now = moment();

    const initMeterDataDisplay = initMeterDataOptions();
    const initStatDataDisplay = initStatData();

    const [displayData, setDisplayData] = useState({
        meterData: initMeterDataDisplay,
        fixData: [],
        statData: initStatDataDisplay,
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
                <>
                    <Box sx={{height: 60, width: '100%'}}>
                        <h2>電力監控資料</h2>
                    </Box>
                </>
                <HighchartsReact
                    highcharts={Highcharts}
                    options={displayData.meterData}
                />
                <>
                    <Box sx={{height: 60, width: '100%'}}>
                        <h2>統計資料</h2>
                    </Box>
                </>
                <TableContainer component={Paper}>
                    <Table sx={{minWidth: 650}} aria-label="Stat Table">
                        <TableHead>
                            <TableRow>
                                <TableCell>統計項目</TableCell>
                                <TableCell align="right">數值</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {displayData.statData.map((stat) => (
                                <TableRow
                                    key={stat.key}
                                    sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                >
                                    <TableCell component="th" scope="row">
                                        {stat.caption}
                                    </TableCell>
                                    <TableCell align="right">{stat.value} {stat.unit}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <>
                    <Box sx={{height: 60, width: '100%'}}>
                        <h2>補值資料</h2>
                    </Box>
                </>
                <Box sx={{height: 1000, width: '100%'}}>
                    <DataGrid
                        rows={displayData.fixData}
                        columns={GRID_COL_DEF}
                        pageSize={100}
                        rowsPerPageOptions={[100]}
                    />
                </Box>
            </Stack>
        </Container>
    );
}

export default App;
