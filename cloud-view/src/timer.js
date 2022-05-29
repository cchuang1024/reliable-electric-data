import {isExists} from "./lang";

export const REFRESH_TIME = 60 * 1000;

const TIMERS = {};

export const buildTimerName = (name, profileId) => `${name}_${profileId}`;

export const Timer = {
    register: (name, interval, callback) => {
        if (!TIMERS[name]) {
            const id = -1;
            const timer = {
                id,
                interval,
                callback,
            };

            // console.log('register timer: ', timer);

            TIMERS[name] = timer;
        } else {
            const timer = TIMERS[name];
            timer.interval = interval;
            timer.callback = callback;
        }
    },

    clearTimerByName: (name) => {
        const timer = TIMERS[name];
        if (isExists(timer)) {
            Timer.clearTimer(name, timer);
        }
    },

    clearTimer: (timerName, timer) => {
        // console.log('clear timer: ', timerName, ' - ', timer);
        clearInterval(timer.id);
        delete TIMERS[timerName];
    },

    clearAll: () => {
        Object.keys(TIMERS).forEach((timerName) => {
            const timer = TIMERS[timerName];
            Timer.clearTimer(timerName, timer);
        });
    },

    clearOthers: (name) => {
        Object.keys(TIMERS).forEach((timerName) => {
            if (timerName !== name) {
                const timer = TIMERS[timerName];
                Timer.clearTimer(timerName, timer);
            }
        });
    },

    run: (name) => {
        if (TIMERS[name]) {
            const timer = TIMERS[name];

            if (TIMERS[name].id === -1) {
                // console.log('run timer: ', timer);

                const id = setInterval(timer.callback, timer.interval);
                timer.id = id;
            }

            Timer.clearOthers(name);
        }
    },

    unregister: (name) => {
        if (TIMERS[name]) {
            TIMERS[name] = undefined;
        }
    },
};

Object.freeze(Timer);

export const updateTimer = (timerName, id, nextUpdater) => {
    if (isExists(timerName) && isExists(id)) {
        const name = buildTimerName(timerName, id);
        Timer.register(name, REFRESH_TIME, nextUpdater);
        Timer.run(name);
    }
    return true;
};

export const removeTimer = (timerName, id) => {
    if (isExists(timerName) && isExists(id)) {
        const name = buildTimerName(timerName, id);
        Timer.clearTimerByName(name);
    }
};