package com.trimaxdevelopers.myqa.utils;

import java.util.Timer;
import java.util.TimerTask;

public abstract class CustomTimer {

    private Timer mTimer;

    private int runningDuration = 0;
    private int duration;

    public CustomTimer(int duration) {
        this.duration = duration;
    }

    public void startTimer() {
        onTimerStart();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runningDuration++;
                onTimerTikMillis(runningDuration);
                if (runningDuration == duration) {
                    stopTimer();
                    runningDuration = 0;
                    onTimeComplete();
                }
            }
        }, 0, 1);
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        onTimerStop();
    }

    public void cancelTimer() {
        runningDuration = 0;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        onTimeCancel();
    }

    public abstract void onTimerStart();

    public abstract void onTimerStop();

    public abstract void onTimerTikMillis(int tik);

    public abstract void onTimeComplete();

    public abstract void onTimeCancel();
}
