package com.swm.heartbeat;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;

import com.swm.core.HeartBeatData;


/**
 * Created by yangzhenyu on 2016/10/6.
 */

public class HeartBeatSound implements HeartBeatListener{
    private Long mLen = 1000L;
    private Handler mSoundHandler;
    private BeatSound mBeatSoundRunnable;
    private ToneGenerator mBeatSound;
    private int mHeartBeatRate;
    private boolean mReleased = false;
    private int mCurrentVolume;

    private class BeatSound implements Runnable {
        protected Long executedTime;

        @Override
        public void run() {
            if(mReleased)
                return;
            mBeatSound.startTone(ToneGenerator.TONE_PROP_BEEP, 50);
            synchronized (mLen) {
                executedTime = System.currentTimeMillis();
                mSoundHandler.postDelayed(this, mLen);
            }
        }
    }

    public HeartBeatSound(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
        mCurrentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        mSoundHandler = new Handler();
        mBeatSound = new ToneGenerator(AudioManager.STREAM_RING, mCurrentVolume);

    }
    @Override
    public void onHeartBeatDataAvailable(HeartBeatData heartBeatData) {
        int heartBeatRate = heartBeatData.heartRate;
        if (heartBeatRate <= 0)
            return;

        if (mHeartBeatRate == heartBeatRate)
            return;

        mHeartBeatRate = heartBeatRate;

        synchronized (mLen) {
            mLen = Long.valueOf(60 * 1000 / mHeartBeatRate);

            if (mBeatSoundRunnable != null) {
                long now  = System.currentTimeMillis();

                if (mBeatSoundRunnable.executedTime < now) {
                    mSoundHandler.removeCallbacks(mBeatSoundRunnable);
                    long elapseTime = now - mBeatSoundRunnable.executedTime;
                    long remain = mLen - elapseTime;
                    mSoundHandler.postDelayed(mBeatSoundRunnable, remain);
                }
            }
        }

        if (mBeatSoundRunnable == null) {
            mBeatSound = new ToneGenerator(AudioManager.STREAM_RING, mCurrentVolume);
            mBeatSoundRunnable = new BeatSound();
            synchronized (mLen) {
                mSoundHandler.postDelayed(mBeatSoundRunnable, mLen);
            }
        }
    }

    public void release() {
        mSoundHandler.removeCallbacks(mBeatSoundRunnable);
        mBeatSoundRunnable = null;
        mBeatSound.release();
    }

}
